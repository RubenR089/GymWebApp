package com.GymWebApp.backend.Service;

import com.GymWebApp.backend.dto.UserRegisterDTO;
import com.GymWebApp.backend.dto.WorkoutHistoryDTO;
import com.GymWebApp.backend.dto.WorkoutSessionResponseDTO;
import com.GymWebApp.backend.dto.WorkoutSetResponseDTO;
import com.GymWebApp.backend.entity.WorkoutPlan;
import com.GymWebApp.backend.repository.*;
import org.springframework.stereotype.Service;
import com.GymWebApp.backend.entity.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WorkoutService {

    private final WorkoutSessionRepository sessionRepository;
    private final WorkoutPlanRepository planRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutSetRepository workoutSetRepository;

    public WorkoutService(WorkoutSessionRepository sessionRepo, WorkoutPlanRepository planRepo, WorkoutLogRepository workoutLogRepository,
                          ExerciseRepository exerciseRepository, WorkoutSetRepository workoutSetRepository) {
        this.sessionRepository = sessionRepo;
        this.planRepository = planRepo;
        this.workoutLogRepository = workoutLogRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutSetRepository = workoutSetRepository;
    }

    public WorkoutSessionResponseDTO startNewSession(Long workoutPlanId) {

        WorkoutPlan p = planRepository.findById(workoutPlanId).orElseThrow(() -> new IllegalArgumentException("Trainingsplan mit ID " + workoutPlanId + " nicht in der DB gefunden"));

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

        WorkoutSet set = new WorkoutSet();
        set.setRepetitions(reps);
        set.setWeight(weight);

        WorkoutLog log;

        Optional<WorkoutLog> optLog = workoutLogRepository.findByWorkoutSessionIdAndExerciseId(sessionId, exerciseId);

        if(optLog.isPresent()){
            log = optLog.get();
        }
        else{
            log = new WorkoutLog();

            WorkoutSession session = sessionRepository.findById(sessionId).orElseThrow();
            Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow();

            log.setWorkoutSession(session);
            log.setExercise(exercise);

            workoutLogRepository.save(log);
        }

        log.addWorkoutSet(set);
        set.setWorkoutLog(log);

        WorkoutSet savedSet = workoutSetRepository.save(set);

        WorkoutSetResponseDTO dto = new WorkoutSetResponseDTO();
        dto.setId(savedSet.getId());
        dto.setRepetitions(savedSet.getRepetitions());
        dto.setWeight(savedSet.getWeight());
        dto.setWorkoutLogId(savedSet.getWorkoutLog().getId());

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

        return dto;
    }

    public List<WorkoutHistoryDTO> getWorkoutHistory(Long userId) {

        List<WorkoutSession> sessions = sessionRepository.findByWorkoutPlanUserId(userId);
        List<WorkoutHistoryDTO> history = new ArrayList<>();

        for(WorkoutSession session : sessions){

            WorkoutHistoryDTO dto = new WorkoutHistoryDTO();

            dto.setStartTime(session.getStartTime());
            dto.setEndTime(session.getEndTime());
            dto.setPlanName(session.getWorkoutPlan().getName());
            dto.setSessionId(session.getId());

            history.add(dto);
        }
        return history;
    }

}