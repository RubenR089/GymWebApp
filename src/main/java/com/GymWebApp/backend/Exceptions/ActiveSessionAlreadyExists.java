package com.GymWebApp.backend.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ActiveSessionAlreadyExists extends RuntimeException {
    public ActiveSessionAlreadyExists(String message) {
        super(message);
    }
}