package com.fosanzdev.trainingBrainAPI.models.appointments;

import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.models.data.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "appointments")
public class Appointment {

    public enum AppointmentStatus {
        PENDING,
        ACCEPTED,
        CANCELLED_BY_PROFESSIONAL,
        CANCELLED_BY_USER,
        COMPLETED
    }


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Instant startDateTime;
    private Instant endDateTime;
    private Instant submissionTime;

    @Column(length = 1000)
    private String submissionNotes;

    @Column(length = 1000)
    private String cancellationReason;

    @Column(length = 1000)
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

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.getId());
        map.put("startDateTime", this.getStartDateTime() != null ? this.getStartDateTime().toString() : null);
        map.put("endDateTime", this.getEndDateTime() != null ? this.getEndDateTime().toString() : null);
        map.put("submissionTime", this.getSubmissionTime() != null ? this.getSubmissionTime().toString() : null);
        map.put("submissionNotes", this.getSubmissionNotes());
        map.put("cancellationReason", this.getCancellationReason());
        map.put("confirmationNotes", this.getConfirmationNotes());
        map.put("professional", this.getProfessional() != null ? this.getProfessional().getId() : null);
        map.put("user", this.getUser() != null ? this.getUser().getId() : null);
        map.put("appointmentStatus", this.getAppointmentStatus() != null ? this.getAppointmentStatus().toString() : null);
        map.put("diagnosis", this.getDiagnosis().toMap() != null ? this.getDiagnosis().toMap() : null);
        return map;
    }
}
