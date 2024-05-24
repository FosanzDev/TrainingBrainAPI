package com.fosanzdev.trainingBrainAPI.repositories.appointments;

import com.fosanzdev.trainingBrainAPI.models.appointments.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, String> {

    @Query("SELECT a FROM Appointment a WHERE a.professional.id = ?1 AND a.endDateTime > CURRENT_TIMESTAMP")
    List<Appointment> findByProfessionalId(String professionalId);

    @Query("SELECT a FROM Appointment a WHERE a.professional.id = ?1 AND a.endDateTime > CURRENT_TIMESTAMP AND a.appointmentStatus = 0") // 0=Pending
    List<Appointment> findPendingAppointmentByProfessionalId(String professionalId);

    @Query("SELECT a FROM Appointment a WHERE a.professional.id = ?1 AND a.endDateTime > CURRENT_TIMESTAMP AND a.appointmentStatus = 1") // 1=Accepted
    List<Appointment> findAcceptedAppointmentByProfessionalId(String professionalId);

    @Query("SELECT a FROM Appointment a WHERE a.professional.id = ?1 AND a.endDateTime > CURRENT_TIMESTAMP AND a.appointmentStatus = 2") // 2=Rejected
    List<Appointment> findRejectedAppointmentByProfessionalId(String professionalId);

    @Query("SELECT a FROM Appointment a WHERE a.user.id = ?1 AND a.endDateTime > CURRENT_TIMESTAMP")
    List<Appointment> findByUserId(String userId);

    @Query("SELECT a FROM Appointment a WHERE a.user.id = ?1 AND a.endDateTime > CURRENT_TIMESTAMP AND a.appointmentStatus = 0") // 0=Pending
    List<Appointment> findPendingAppointmentByUserId(String userId);

    @Query("SELECT a FROM Appointment a WHERE a.user.id = ?1 AND a.endDateTime > CURRENT_TIMESTAMP AND a.appointmentStatus = 1") // 1=Accepted
    List<Appointment> findAcceptedAppointmentByUserId(String userId);

    @Query("SELECT a FROM Appointment a WHERE a.user.id = ?1 AND a.endDateTime > CURRENT_TIMESTAMP AND a.appointmentStatus = 2") // 2=Rejected
    List<Appointment> findRejectedAppointmentByUserId(String userId);
}
