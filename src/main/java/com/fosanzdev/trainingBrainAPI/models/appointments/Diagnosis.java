package com.fosanzdev.trainingBrainAPI.models.appointments;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "diagnosis")
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String header;
    private String shortDescription;
    private String description;

    private String recommendation;
    private String treatment;

    @OneToOne(mappedBy = "diagnosis", cascade = {CascadeType.REFRESH})
    private Appointment appointment;
}
