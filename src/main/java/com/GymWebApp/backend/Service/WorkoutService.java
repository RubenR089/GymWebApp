package com.GymWebApp.backend.Service;

import com.GymWebApp.backend.entity.WorkoutPlan;
import com.GymWebApp.backend.repository.*;
import org.springframework.stereotype.Service;
import com.GymWebApp.backend.entity.*;

import java.time.LocalDateTime;

@Service
public class WorkoutService {

    private final WorkoutSessionRepository sessionRepository;
    private final WorkoutPlanRepository planRepository;

    public WorkoutService(WorkoutSessionRepository sessionRepo, WorkoutPlanRepository planRepo) {
        this.sessionRepository = sessionRepo;
        this.planRepository = planRepo;

    }

    public WorkoutSession startNewSession(Long userId, Long workoutPlanId) {

        WorkoutPlan p = planRepository.findById(workoutPlanId).orElseThrow(() -> new IllegalArgumentException("Trainingsplan mit ID " + workoutPlanId + " nicht in der DB gefunden"));

        WorkoutSession session = new WorkoutSession();

        session.setWorkoutPlan(p);
        session.setStartTime(LocalDateTime.now());

        sessionRepository.save(session);

        return session;
    }

}
