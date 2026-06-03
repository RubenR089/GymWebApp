package com.GymWebApp.backend.controller;

import com.GymWebApp.backend.Service.WorkoutService;
import com.GymWebApp.backend.dto.*;
import com.GymWebApp.backend.entity.Exercise;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
@CrossOrigin(origins = "*")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }


    @PostMapping("/start")
    public WorkoutSessionResponseDTO startSession(@RequestParam Long workoutPlanId) {

        return workoutService.startNewSession(workoutPlanId);
    }

    @PostMapping("/add-set")
    public WorkoutSetResponseDTO workoutSetRespone(@RequestParam Long sessionId, @RequestParam Long exerciseId, @RequestParam double weight, @RequestParam int reps) {
        return workoutService.addSetToWorkout(sessionId, exerciseId, weight, reps);
    }

    @PostMapping("/end")
    public WorkoutSessionResponseDTO endSession(@RequestParam Long sessionId) {
        return workoutService.endWorkoutSession(sessionId);
    }

    @GetMapping("/history")
    public List<WorkoutHistoryDTO> getHistory(@RequestParam Long userId) {
        return workoutService.getWorkoutHistory(userId);
    }

    @PostMapping("/create-plan")
    public void createPlan(@RequestBody WorkOutPlanDTO workoutPlanDTO) {
       workoutService.createWorkOutPlan(workoutPlanDTO);
    }

    @PostMapping("/add-exercise")
    public void addExercise(@RequestBody WorkoutExerciseDTO dto)
    {
        workoutService.addExerciseToPlan(dto);
    }

    @PostMapping("/create-exercise")
    public Exercise createExerciseWithPlan(@RequestBody WorkoutExerciseDTO dto) {
        return workoutService.createExerciseWithPlan(dto);
    }

    @GetMapping("/plans")
    public List<WorkOutPlanDTO> getPlans(@RequestParam Long userId) {
        return workoutService.getPlansByUserId(userId);
    }

}
