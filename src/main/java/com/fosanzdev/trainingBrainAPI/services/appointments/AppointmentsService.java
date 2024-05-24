package com.fosanzdev.trainingBrainAPI.services.appointments;

import com.fosanzdev.trainingBrainAPI.models.appointments.Appointment;
import com.fosanzdev.trainingBrainAPI.models.appointments.ProfessionalHoliday;
import com.fosanzdev.trainingBrainAPI.models.appointments.ProfessionalSchedule;
import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.repositories.appointments.AppointmentRepository;
import com.fosanzdev.trainingBrainAPI.repositories.appointments.ProfessionalHolidaysRepository;
import com.fosanzdev.trainingBrainAPI.repositories.appointments.ProfessionalScheduleRepository;
import com.fosanzdev.trainingBrainAPI.services.interfaces.appointments.IAppointmentsService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class AppointmentsService implements IAppointmentsService {

    @Autowired
    private ProfessionalHolidaysRepository professionalHolidaysRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ProfessionalScheduleRepository professionalScheduleRepository;

    @Transactional
    @Override
    public boolean bookAppointment(Appointment appointment) throws AppointmentException{
        Professional professional = appointment.getProfessional();

        List<ProfessionalHoliday> holidays = professionalHolidaysRepository.findByProfessionalId(professional.getId());
        List<ProfessionalSchedule> schedules = professionalScheduleRepository.findByProfessionalId(professional.getId());
        List<Appointment> acceptedAppointments = appointmentRepository.findAcceptedAppointmentByProfessionalId(professional.getId());
        List<Appointment> pendingAppointments = appointmentRepository.findPendingAppointmentByProfessionalId(professional.getId());

        // Convert Instant to ZonedDateTime
        ZonedDateTime startDateTime = appointment.getStartDateTime().atZone(ZoneId.systemDefault());
        ZonedDateTime endDateTime = appointment.getEndDateTime().atZone(ZoneId.systemDefault());

        // Check if the difference between startDateTime and endDateTime is a multiple of professionalSchedule.intervalMinutes
        // and that the start and end are on the same day
        long duration = Duration.between(startDateTime, endDateTime).toMinutes();
        if (duration % schedules.get(0).getIntervalMinutes() != 0 || duration / schedules.get(0).getIntervalMinutes() > 2 ||
                !startDateTime.toLocalDate().equals(endDateTime.toLocalDate())) {
            throw new AppointmentException("Invalid appointment duration");
        }


        // Check if there are no holiday conflicts with the appointment
        if (holidays.stream().anyMatch(
                holiday -> !appointment.getStartDateTime().isAfter(holiday.getEndDateTime()) &&
                        !appointment.getEndDateTime().isBefore(holiday.getStartDateTime())
        )) {
            throw new AppointmentException("Appointment conflicts with a holiday");
        }

        // Check if the user does not have any other accepted or pending appointment with the same professional or does not conflict with other appointments
        if (acceptedAppointments.stream().anyMatch(
                acceptedAppointment -> acceptedAppointment.getUser().equals(appointment.getUser()) &&
                        !appointment.getStartDateTime().isAfter(acceptedAppointment.getEndDateTime()) &&
                        !appointment.getEndDateTime().isBefore(acceptedAppointment.getStartDateTime())
        )) {
            throw new AppointmentException("User has another accepted appointment with the same professional");
        }

        if (pendingAppointments.stream().anyMatch(
                pendingAppointment -> pendingAppointment.getUser().equals(appointment.getUser()) &&
                        !appointment.getStartDateTime().isAfter(pendingAppointment.getEndDateTime()) &&
                        !appointment.getEndDateTime().isBefore(pendingAppointment.getStartDateTime())
        )) {
            throw new AppointmentException("User has another pending appointment with the same professional");
        }

        // Check if the appointment is within the schedule
        boolean isWithinSchedule = false;
        for (ProfessionalSchedule schedule : schedules) {
            // Convert Instant to LocalTime
            LocalTime startHour = schedule.getStartHour().atZone(ZoneId.systemDefault()).toLocalTime();
            LocalTime endHour = schedule.getEndHour().atZone(ZoneId.systemDefault()).toLocalTime();

            if (startDateTime.getDayOfWeek().getValue() == schedule.getDayOfWeek() &&
                    !startDateTime.toLocalTime().isBefore(startHour) &&
                    !endDateTime.toLocalTime().isAfter(endHour)) {
                isWithinSchedule = true;
                break;
            }
        }
        if (!isWithinSchedule) {
            throw new AppointmentException("Appointment is not within the professional's schedule");
        }

        // Check if the appointment does not overlap with other accepted appointments
        if (acceptedAppointments.stream().anyMatch(
                acceptedAppointment -> !appointment.getStartDateTime().isAfter(acceptedAppointment.getEndDateTime()) &&
                        !appointment.getEndDateTime().isBefore(acceptedAppointment.getStartDateTime())
        )) {
            throw new AppointmentException("Appointment conflicts with another accepted appointment");
        }

        // If all checks pass, save the appointment
        appointmentRepository.save(appointment);
        return true;
    }

    @Override
    public boolean acceptAppointment(String appointmentId, String professionalComment) {
        return false;
    }

    @Override
    public boolean rejectAppointment(String appointmentId, String professionalComment) {
        return false;
    }
}
