package com.GymWebApp.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.GymWebApp.backend.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
