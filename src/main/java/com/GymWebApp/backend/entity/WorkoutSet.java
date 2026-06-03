package com.GymWebApp.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "workout_sets")
@Getter
@Setter
public class WorkoutSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int setNumber = 0;

    @Column(nullable = false)
    private double weight;

    @Column(nullable = false)
    private int repetitions;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "workout_log_id", nullable = false)
    private WorkoutLog workoutLog;

}
