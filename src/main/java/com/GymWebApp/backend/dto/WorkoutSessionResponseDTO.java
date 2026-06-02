package com.GymWebApp.backend.dto;

import lombok.Setter;
import lombok.Getter;
import java.time.LocalDateTime;


@Getter
@Setter
public class WorkoutSessionResponseDTO {

    private Long id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String planName;

}
