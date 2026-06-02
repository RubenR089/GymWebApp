package com.GymWebApp.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkoutSetResponseDTO {
    private Long id;
    private int repetitions;
    private double weight;
    private Long workoutLogId;
}