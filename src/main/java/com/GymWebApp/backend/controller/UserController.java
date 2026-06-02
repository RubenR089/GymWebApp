package com.GymWebApp.backend.controller;

import com.GymWebApp.backend.Exceptions.UsernameTakenException;
import com.GymWebApp.backend.Service.UserService;
import com.GymWebApp.backend.dto.UserRegisterDTO;
import com.GymWebApp.backend.entity.User;
import com.GymWebApp.backend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserRegisterDTO registerUser(@RequestBody UserRegisterDTO dto) throws UsernameTakenException {
        return userService.registerUser(dto);
    }

    @PostMapping("/login")
    public boolean userLogIn(@RequestBody UserRegisterDTO loginDTO) {
        return userService.userLogIn(loginDTO);
    }

}
