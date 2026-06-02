package com.GymWebApp.backend.controller;

import com.GymWebApp.backend.Service.WorkoutService;
import com.GymWebApp.backend.dto.WorkoutSessionResponseDTO;
import com.GymWebApp.backend.dto.WorkoutSetResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }


    @PostMapping("/start")
    public WorkoutSessionResponseDTO startSession(@RequestParam Long userId, @RequestParam Long workoutPlanId) {

        return workoutService.startNewSession(userId, workoutPlanId);
    }

    @PostMapping("/add-set")
    public WorkoutSetResponseDTO workoutSetRespone(@RequestParam Long sessionId, @RequestParam Long exerciseId, @RequestParam double weight, @RequestParam int reps) {
        return workoutService.addSetToWorkout(sessionId, exerciseId, weight, reps);
    }
}
