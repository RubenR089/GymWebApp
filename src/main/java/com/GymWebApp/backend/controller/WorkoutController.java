package com.GymWebApp.backend.controller;

import com.GymWebApp.backend.Service.WorkoutService;
import com.GymWebApp.backend.entity.WorkoutSession;
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
    public WorkoutSession startSession(@RequestParam Long userId, @RequestParam Long workoutPlanId) {

        WorkoutSession session = workoutService.startNewSession(userId, workoutPlanId);

        return session;
    }
}
