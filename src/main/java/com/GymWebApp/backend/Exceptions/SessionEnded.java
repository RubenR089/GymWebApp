package com.GymWebApp.backend.Exceptions;

public class SessionEnded extends RuntimeException {

    public SessionEnded(String message) {
        super(message);
    }
}
