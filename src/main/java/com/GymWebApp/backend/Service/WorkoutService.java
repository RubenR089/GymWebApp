package com.GymWebApp.backend.Service;

import com.GymWebApp.backend.Exceptions.ActiveSessionAlreadyExists;
import com.GymWebApp.backend.Exceptions.ExerciseAlreadyExists;
import com.GymWebApp.backend.Exceptions.SessionEnded;
import com.GymWebApp.backend.dto.WorkoutHistoryDTO;
import com.GymWebApp.backend.dto.WorkoutSessionResponseDTO;
import com.GymWebApp.backend.dto.WorkoutSetResponseDTO;
import com.GymWebApp.backend.entity.WorkoutPlan;
import com.GymWebApp.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.GymWebApp.backend.entity.*;
import com.GymWebApp.backend.dto.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WorkoutService {

    private final WorkoutSessionRepository sessionRepository;
    private final WorkoutPlanRepository planRepository;
    private final WorkoutLogRepository logRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutSetRepository setRepository;
    private final UserRepository userRepository;

    public WorkoutService(WorkoutSessionRepository sessionRepository, WorkoutPlanRepository planRepository, WorkoutLogRepository logRepository,
                          ExerciseRepository exerciseRepository, WorkoutSetRepository setRepository,  UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.planRepository = planRepository;
        this.logRepository = logRepository;
        this.exerciseRepository = exerciseRepository;
        this.setRepository = setRepository;
        this.userRepository = userRepository;
    }

    public WorkoutSessionResponseDTO startNewSession(Long workoutPlanId) {


        WorkoutPlan p = planRepository.findById(workoutPlanId).orElseThrow(() -> new IllegalArgumentException("Trainingsplan mit ID " + workoutPlanId + " nicht in der DB gefunden"));

        Long userId = p.getUser().getId();
        if (sessionRepository.existsByWorkoutPlanUserIdAndEndTimeIsNull(userId)) {
            throw new ActiveSessionAlreadyExists("Du hast bereits ein aktives Workout laufen! Beende das erst, bevor du ein neues startest.");
        }

        WorkoutSession session = new WorkoutSession();
        session.setWorkoutPlan(p);
        session.setStartTime(LocalDateTime.now());

        sessionRepository.save(session);

        WorkoutSessionResponseDTO dto = new WorkoutSessionResponseDTO();
        dto.setPlanName(p.getName());
        dto.setId(session.getId());
        dto.setStartTime(session.getStartTime());

        return dto;
    }

    public WorkoutSetResponseDTO addSetToWorkout(Long sessionId, Long exerciseId, double weight, int reps){

        if(hasSessionEnded(sessionId)){
            throw new SessionEnded("Die Session ist bereits beendet!");
        }

        WorkoutSet set = new WorkoutSet();
        if(weight <= 0 || reps <= 0){
            throw new IllegalArgumentException("Verschrieben? Negative zahlen sind unzuöässig");
        }
        set.setRepetitions(reps);
        set.setWeight(weight);

        WorkoutLog log;

        Optional<WorkoutLog> optLog = logRepository.findByWorkoutSessionIdAndExerciseId(sessionId, exerciseId);

        if(optLog.isPresent()){
            log = optLog.get();
        }
        else{
            log = new WorkoutLog();

            WorkoutSession session = sessionRepository.findById(sessionId).orElseThrow();
            Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow();

            log.setWorkoutSession(session);
            log.setExercise(exercise);

            logRepository.save(log);
        }

        log.getSets().add(set);
        set.setWorkoutLog(log);
        set.setSetNumber(log.getSets().size());

        WorkoutSet savedSet = setRepository.save(set);

        WorkoutSetResponseDTO dto = new WorkoutSetResponseDTO();
        dto.setId(savedSet.getId());
        dto.setRepetitions(savedSet.getRepetitions());
        dto.setWeight(savedSet.getWeight());
        dto.setWorkoutLogId(savedSet.getWorkoutLog().getId());
        dto.setSetNumber(savedSet.getSetNumber());

        return dto;
    }

    public WorkoutSessionResponseDTO endWorkoutSession(Long sessionId) {

        WorkoutSession session = sessionRepository.findById(sessionId).orElseThrow();

        session.setEndTime(LocalDateTime.now());

        sessionRepository.save(session);

        WorkoutSessionResponseDTO dto = new WorkoutSessionResponseDTO();
        dto.setId(session.getId());
        dto.setEndTime(session.getEndTime());
        dto.setPlanName(session.getWorkoutPlan().getName());
        dto.setStartTime(session.getStartTime());
        dto.setTimePassed(Duration.between(dto.getStartTime(), dto.getEndTime()).toMinutes());

        return dto;
    }

    @Transactional(readOnly = true)
    public List<WorkoutHistoryDTO> getWorkoutHistory(Long userId) {

        List<WorkoutSession> sessions = sessionRepository.findByWorkoutPlanUserId(userId);
        List<WorkoutHistoryDTO> history = new ArrayList<>();

        for(WorkoutSession session : sessions){

            WorkoutHistoryDTO dto = new WorkoutHistoryDTO();

            dto.setStartTime(session.getStartTime());
            dto.setEndTime(session.getEndTime());
            dto.setPlanName(session.getWorkoutPlan().getName());
            dto.setSessionId(session.getId());

            List<ExerciseHistoryDTO> exercises = new ArrayList<>();

            for(WorkoutLog log : session.getWorkoutLogs()){
                ExerciseHistoryDTO exerciseDto = new ExerciseHistoryDTO();
                exerciseDto.setName(log.getExercise().getName());

                List<SetHistoryDTO> setHistoryDto = new ArrayList<>();

                for(WorkoutSet set : log.getSets()){

                    SetHistoryDTO setDto = new SetHistoryDTO();
                    setDto.setReps(set.getRepetitions());
                    setDto.setWeight(set.getWeight());
                    setDto.setSetNumber(set.getSetNumber());
                    setHistoryDto.add(setDto);
                }

                exerciseDto.setSetHistory(setHistoryDto);
                exercises.add(exerciseDto);
            }

            dto.setExercises(exercises);
            history.add(dto);
        }
        return history;
    }

    @Transactional
    public void createWorkOutPlan(WorkOutPlanDTO dto) {

        User u = userRepository.findById(dto.getUserId()).orElseThrow();

        WorkoutPlan plan = new WorkoutPlan();
        plan.setName(dto.getName());

        u.getWorkoutPlans().add(plan);
        plan.setUser(u);

        planRepository.save(plan);
    }


    public void addExerciseToPlan(WorkoutExerciseDTO dto) {

        WorkoutPlan p = planRepository.findById(dto.getPlanId()).orElseThrow();

        Exercise exercise = exerciseRepository.findById(dto.getExerciseId()).orElseThrow();

        p.getExercises().add(exercise);

        planRepository.save(p);
    }


   public Exercise createExerciseWithPlan(WorkoutExerciseDTO dto) {

        WorkoutPlan plan = planRepository.findById(dto.getPlanId()).orElseThrow();

        Exercise exercise = new Exercise();

        if(exerciseRepository.existsByName(dto.getExerciseName())){
            throw new ExerciseAlreadyExists(dto.getExerciseName());
        }

        exercise.setName(dto.getExerciseName());
        exercise.setDescription(dto.getExerciseDescription());
        exercise.setWorkoutPlan(plan);

        exerciseRepository.save(exercise);

        return exercise;
    }

    public boolean hasSessionEnded(Long sessionId){
        if(sessionRepository.findById(sessionId).isPresent() && sessionRepository.findById(sessionId).get().getEndTime() == null){
            return false;
        }
        return true;
    }

    @Transactional(readOnly = true)
    public List<WorkOutPlanDTO> getPlansByUserId(Long userId) {
        List<WorkoutPlan> plans = planRepository.findByUserId(userId);
        List<WorkOutPlanDTO> dtos = new ArrayList<>();

        for (WorkoutPlan plan : plans) {
            WorkOutPlanDTO dto = new WorkOutPlanDTO();
            dto.setUserId(userId);
            dto.setName(plan.getName());
            dto.setId(plan.getId());

            List<WorkoutExerciseDTO> exerciseDtos = new ArrayList<>();
            for (Exercise exercise : plan.getExercises()) {
                WorkoutExerciseDTO exDto = new WorkoutExerciseDTO();
                exDto.setExerciseId(exercise.getId());
                exDto.setExerciseName(exercise.getName());
                exDto.setExerciseDescription(exercise.getDescription());
                exDto.setPlanId(plan.getId());
                exerciseDtos.add(exDto);
            }
            dto.setExercises(exerciseDtos);

            dtos.add(dto);
        }
        return dtos;
    }

}