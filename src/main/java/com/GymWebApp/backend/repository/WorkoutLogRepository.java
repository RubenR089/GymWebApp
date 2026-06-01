package com.GymWebApp.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.GymWebApp.backend.entity.WorkoutLog;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {
}
