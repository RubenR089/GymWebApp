package com.GymWebApp.backend;

import com.GymWebApp.backend.entity.User;
import com.GymWebApp.backend.entity.WorkoutPlan;
import com.GymWebApp.backend.entity.Exercise;
import com.GymWebApp.backend.repository.UserRepository;
import com.GymWebApp.backend.repository.WorkoutPlanRepository;
import com.GymWebApp.backend.repository.ExerciseRepository;
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
    public CommandLineRunner initDatabase(
            UserRepository userRepo,
            WorkoutPlanRepository planRepo,
            ExerciseRepository exerciseRepo) {
        return args -> {
            // 1. Einen Test-User anlegen
            User user = new User();
            user.setUsername("GymBro123");
            user.setPassword("safePassword");
            userRepo.save(user);

            // 2. Einen Test-Trainingsplan für diesen User anlegen
            WorkoutPlan plan = new WorkoutPlan();
            plan.setName("Push Day (Brust/Schulter/Trizeps)");
            plan.setUser(user);
            planRepo.save(plan);

            // 3. Eine Test-Übung anlegen und RECHTZEITIG mit dem Plan verknüpfen
            Exercise exercise = new Exercise();
            exercise.setName("Bankdrücken");
            exercise.setDescription("Flachbank mit der Langhantel");
            exercise.setWorkoutPlan(plan); // Das verhindert die PropertyValueException!
            exerciseRepo.save(exercise);

            System.out.println("\n==================================================================");
            System.out.println(">>> TESTDATEN ERFOLGREICH ANGELEGT!");
            System.out.println(">>> User-ID: " + user.getId() + " (" + user.getUsername() + ")");
            System.out.println(">>> Plan-ID: " + plan.getId() + " (" + plan.getName() + ")");
            System.out.println(">>> Exercise-ID: " + exercise.getId() + " (" + exercise.getName() + ")");
            System.out.println("==================================================================\n");
        };
    }
}