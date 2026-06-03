package com.GymWebApp.backend.dto;

import com.GymWebApp.backend.entity.WorkoutPlan;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkoutExerciseDTO {

    private Long planId;

    private String exerciseName;

    private String exerciseDescription;

    private Long exerciseId;

}
