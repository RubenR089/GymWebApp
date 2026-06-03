package com.GymWebApp.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.GymWebApp.backend.entity.WorkoutLog;
import java.util.Optional;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {

    Optional<WorkoutLog> findByWorkoutSessionIdAndExerciseId(Long sessionId, Long exerciseId);

}
