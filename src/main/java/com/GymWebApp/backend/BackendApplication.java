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
            exercise.setWorkoutPlan(plan);
            exerciseRepo.save(exercise);

            // DER TRICK: Absoluten Pfad der index.html ermitteln (wenn sie im static-Ordner liegt)
            // Falls sie woanders liegt, kannst du den String unten auch hart codieren
            String frontendUrl = "file:///" + System.getProperty("user.dir").replace("\\", "/")
                    + "/src/main/resources/static/index.html";

            System.out.println("\n==================================================================");
            System.out.println("🚀 GYM-WEBAPP BACKEND ERFOLGREICH GESTARTET!");
            System.out.println("==================================================================");
            System.out.println(">>> Test-Daten in der H2-Datenbank generiert:");
            System.out.println("    • User-ID:     " + user.getId() + " [" + user.getUsername() + "]");
            System.out.println("    • Plan-ID:     " + plan.getId() + " [" + plan.getName() + "]");
            System.out.println("    • Exercise-ID: " + exercise.getId() + " [" + exercise.getName() + "]");
            System.out.println("------------------------------------------------------------------");
            System.out.println("👉 ÖFFNE DAS FRONTEND PER KLICK / DOPPELKLICK IN INTELLIJ:");
            System.out.println("   " + frontendUrl);
            System.out.println("==================================================================\n");
        };
    }
}