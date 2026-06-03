package com.GymWebApp.backend.Service;

import com.GymWebApp.backend.Exceptions.UsernameTakenException;
import com.GymWebApp.backend.dto.UserRegisterDTO;
import com.GymWebApp.backend.entity.User;
import com.GymWebApp.backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,  BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserRegisterDTO registerUser(@RequestBody UserRegisterDTO dto) throws UsernameTakenException {

        User user = new User();
        if(userRepository.findByUsername(dto.getUsername()).isPresent()){
            throw new UsernameTakenException("Username ist vergeben");
        }

        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User savedUser = userRepository.save(user);

        UserRegisterDTO responseDTO = new UserRegisterDTO();
        responseDTO.setUsername(savedUser.getUsername());
        responseDTO.setUsername(user.getUsername());

        return responseDTO;
    }

    public boolean userLogIn(UserRegisterDTO dto) {

        Optional<User> user = userRepository.findByUsername(dto.getUsername());

        if(user.isPresent() && passwordEncoder.matches(dto.getPassword() ,user.get().getPassword())) {
            System.out.println("User " + dto.getUsername() + "logged in");
            return true;
        }
        return false;
    }



}
