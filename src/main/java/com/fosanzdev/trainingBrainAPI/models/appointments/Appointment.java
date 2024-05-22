package com.fosanzdev.trainingBrainAPI.models.appointments;

import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.models.details.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "appointments")
public class Appointment {

    private enum AppointmentStatus {
        PENDING,
        CONFIRMED,
        CANCELLED
    }


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Instant startDateTime;
    private Instant endDateTime;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_professional", referencedColumnName = "id")
    private Professional professional;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_user", referencedColumnName = "id")
    private User user;

    private AppointmentStatus appointmentStatus;
}
