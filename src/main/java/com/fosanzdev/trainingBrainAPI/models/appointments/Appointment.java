package com.fosanzdev.trainingBrainAPI.models.appointments;

import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.models.details.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

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
    private Instant submissionTime;

    private String submissionNotes;
    private String cancellationReason;
    private String confirmationNotes;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_professional", referencedColumnName = "id")
    private Professional professional;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_user", referencedColumnName = "id")
    private User user;

    @OneToOne
    @JoinColumn(name = "fk_diagnosis", referencedColumnName = "id")
    private Diagnosis diagnosis;

    private AppointmentStatus appointmentStatus;

    public static Appointment fromMap(Map<String, Object> jsonMap) {
        Appointment appointment = new Appointment();
        appointment.setStartDateTime(Instant.parse((String) jsonMap.get("startDateTime")));
        appointment.setEndDateTime(Instant.parse((String) jsonMap.get("endDateTime")));
        appointment.setSubmissionTime(Instant.now());
        appointment.setSubmissionNotes((String) jsonMap.get("submissionNotes"));
        appointment.setAppointmentStatus(AppointmentStatus.PENDING);
        return appointment;
    }
}
