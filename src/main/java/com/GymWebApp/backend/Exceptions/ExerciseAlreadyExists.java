package com.GymWebApp.backend.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ExerciseAlreadyExists extends RuntimeException {

    public ExerciseAlreadyExists(String name) {
        super("Exercise already exists: " + name);
    }

}
