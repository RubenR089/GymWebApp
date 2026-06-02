package com.GymWebApp.backend.dto;

import com.GymWebApp.backend.entity.WorkoutPlan;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class WorkoutHistoryDTO {

    private Long sessionId;

    private String planName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}
