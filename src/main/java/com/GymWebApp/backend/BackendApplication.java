package com.GymWebApp.backend;

import com.GymWebApp.backend.entity.User;
import com.GymWebApp.backend.entity.WorkoutPlan;
import com.GymWebApp.backend.repository.UserRepository;
import com.GymWebApp.backend.repository.WorkoutPlanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepo, WorkoutPlanRepository planRepo) {
        return args -> {
            // 1. Einen Test-User anlegen
            User user = new User();
            user.setUsername("GymBro123");
            user.setPassword("safePassword"); // Später verschlüsseln wir das ordentlich
            user.setHeight(180); // Nur als Beispielwert
            userRepo.save(user);

            // 2. Einen Test-Trainingsplan für diesen User anlegen
            WorkoutPlan plan = new WorkoutPlan();
            plan.setName("Push Day (Brust/Schulter/Trizeps)");
            plan.setUser(user); // Hier verknüpfen wir den Plan mit dem User!
            planRepo.save(plan);

            System.out.println("\n==================================================================");
            System.out.println(">>> TESTDATEN ERFOLGREICH ANGELEGT!");
            System.out.println(">>> User-ID: " + user.getId() + " (" + user.getUsername() + ")");
            System.out.println(">>> Plan-ID: " + plan.getId() + " (" + plan.getName() + ")");
            System.out.println("==================================================================\n");
        };
    }
}