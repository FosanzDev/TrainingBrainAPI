package com.fosanzdev.trainingBrainAPI.models.goals;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "routine_entries")
public class RoutineEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Instant submissionDateTime;
}
