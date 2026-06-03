package com.GymWebApp.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExerciseHistoryDTO {

    private String name;

    private List<SetHistoryDTO> setHistory;

}
