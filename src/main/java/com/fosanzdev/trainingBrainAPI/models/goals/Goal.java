package com.fosanzdev.trainingBrainAPI.models.goals;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 100)
    private String title;
    @Column(length = 1600)
    private String description;

    private boolean completed;
    private int hoursBetween;
    private int repetitions;

    private Instant startDateTime;
}
