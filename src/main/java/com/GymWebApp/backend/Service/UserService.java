package com.GymWebApp.backend.Service;

import com.GymWebApp.backend.Exceptions.UsernameTakenException;
import com.GymWebApp.backend.dto.UserRegisterDTO;
import com.GymWebApp.backend.entity.User;
import com.GymWebApp.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    public UserRegisterDTO registerUser(@RequestBody UserRegisterDTO dto) throws UsernameTakenException {

        User user = new User();
        if(userRepository.findByUsername(dto.getUsername()).isPresent()){
            throw new UsernameTakenException("Username ist vergeben");
        }

        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());

        User savedUser = userRepository.save(user);

        UserRegisterDTO responseDTO = new UserRegisterDTO();
        responseDTO.setUsername(savedUser.getUsername());
        responseDTO.setUsername(user.getUsername());

        return responseDTO;
    }
}
