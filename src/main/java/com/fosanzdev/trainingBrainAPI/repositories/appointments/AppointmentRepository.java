package com.fosanzdev.trainingBrainAPI.repositories.appointments;

import com.fosanzdev.trainingBrainAPI.models.appointments.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, String> {
}
