package com.fosanzdev.trainingBrainAPI.services.interfaces.appointments;

import com.fosanzdev.trainingBrainAPI.models.appointments.Appointment;
import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.models.details.User;
import com.fosanzdev.trainingBrainAPI.services.appointments.AppointmentException;

import java.time.Instant;

public interface IAppointmentsService {

    public boolean bookAppointment (Appointment appointment) throws AppointmentException;
    public boolean acceptAppointment (String appointmentId, String professionalComment);
    public boolean rejectAppointment (String appointmentId, String professionalComment);
}
