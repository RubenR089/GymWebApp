package com.GymWebApp.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.GymWebApp.backend.entity.WorkoutPlan;

import java.util.List;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {

    List<WorkoutPlan> findByUserId(Long userId);

}

