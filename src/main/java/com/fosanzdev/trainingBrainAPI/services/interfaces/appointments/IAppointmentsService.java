package com.fosanzdev.trainingBrainAPI.services.interfaces.appointments;

import com.fosanzdev.trainingBrainAPI.models.appointments.Appointment;
import com.fosanzdev.trainingBrainAPI.models.appointments.Diagnosis;
import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.models.details.User;
import com.fosanzdev.trainingBrainAPI.services.appointments.AppointmentException;

import java.util.List;

public interface IAppointmentsService {

    boolean isConflictive (Appointment appointment) throws AppointmentException;
    void rejectAllConflictingAppointments(Appointment appointment);
    void bookAppointment (Appointment appointment) throws AppointmentException;
    void acceptAppointment (String appointmentId, String professionalComment) throws AppointmentException;
    void rejectAppointment (User user, String appointmentId, String professionalComment) throws AppointmentException;
    void rejectAppointment(Professional professional, String appointmentId, String professionalComment) throws AppointmentException;
    List<Appointment> getAppointmentsByStatus(User user, String status);
    List<Appointment> getAppointmentsByStatus(Professional professional, String status);
    Appointment getAppointmentById(User user, String appointmentId);
    Appointment getAppointmentById(Professional professional, String appointmentId);
    boolean markAsCompleted(Professional professional, String appointmentId, Diagnosis diagnosis) throws AppointmentException;
}
