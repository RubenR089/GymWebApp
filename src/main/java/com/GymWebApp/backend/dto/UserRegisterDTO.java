package com.GymWebApp.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDTO {

    private String username;

    private String password;

    private Long userId;
}
