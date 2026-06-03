package com.GymWebApp.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WorkoutHistoryDTO {

    private Long sessionId;

    private String planName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private List<ExerciseHistoryDTO> exercises = new ArrayList<>();

}
