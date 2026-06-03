package com.GymWebApp.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.GymWebApp.backend.entity.WorkoutSession;
import java.util.List;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {

    List<WorkoutSession> findByWorkoutPlanUserId(Long userId);

    boolean existsByWorkoutPlanUserIdAndEndTimeIsNull(Long userId);
}