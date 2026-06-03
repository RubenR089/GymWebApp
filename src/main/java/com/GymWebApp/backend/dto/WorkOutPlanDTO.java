package com.GymWebApp.backend.dto;

import lombok.Getter;
import lombok.Setter;
import com.GymWebApp.backend.entity.*;
import java.util.List;

@Getter
@Setter
public class WorkOutPlanDTO {

    private String name;

    private Long userId;

    private Long id;

    private List<WorkoutExerciseDTO> exercises;
}
