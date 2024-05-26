package com.fosanzdev.trainingBrainAPI.services.appointments;

import com.fosanzdev.trainingBrainAPI.models.appointments.Appointment;
import com.fosanzdev.trainingBrainAPI.models.appointments.Diagnosis;
import com.fosanzdev.trainingBrainAPI.models.appointments.ProfessionalHoliday;
import com.fosanzdev.trainingBrainAPI.models.appointments.ProfessionalSchedule;
import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.models.data.User;
import com.fosanzdev.trainingBrainAPI.repositories.appointments.AppointmentRepository;
import com.fosanzdev.trainingBrainAPI.repositories.appointments.DiagnosisRepository;
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
import java.util.Objects;

@Service
public class AppointmentsService implements IAppointmentsService {

    @Autowired
    private ProfessionalHolidaysRepository professionalHolidaysRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ProfessionalScheduleRepository professionalScheduleRepository;

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @Transactional
    @Override
    public void isConflictive(Appointment appointment, boolean checkForOverlaps) throws AppointmentException {
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

        if (checkForOverlaps) {

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
    }

    @Transactional
    @Override
    public void bookAppointment(Appointment appointment) throws AppointmentException {

        try {
            //Check if the appointment start time does not exceed 2 weeks from the current time
            if (appointment.getStartDateTime().isAfter(ZonedDateTime.now().plusWeeks(2).toInstant())) {
                throw new AppointmentException("Appointment start time exceeds 2 weeks from the current time");
            }
            isConflictive(appointment, true);
        } catch (AppointmentException e) {
            throw new AppointmentException(e.toString());
        }

        appointmentRepository.save(appointment);
    }

    @Transactional
    @Override
    public void rejectAllConflictingAppointments(Appointment appointment) {
        List<Appointment> conflictingAppointments = appointmentRepository.findPendingAppointmentByProfessionalId(appointment.getProfessional().getId());

        for (Appointment conflictingAppointment : conflictingAppointments) {
            if (Objects.equals(appointment.getId(), conflictingAppointment.getId())) continue;
            try {
                isConflictive(conflictingAppointment, true);
            } catch (AppointmentException e) {
                conflictingAppointment.setAppointmentStatus(Appointment.AppointmentStatus.CANCELLED_BY_PROFESSIONAL);
                conflictingAppointment.setCancellationReason("Conflicts with another appointment");
                appointmentRepository.save(conflictingAppointment);
            }
        }
    }


    @Transactional
    @Override
    public void acceptAppointment(String appointmentId, String professionalComment) throws AppointmentException {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment == null) throw new AppointmentException("Appointment not found");

        if (appointment.getAppointmentStatus() != Appointment.AppointmentStatus.PENDING)
            throw new AppointmentException("Appointment is not pending");

        try {
            isConflictive(appointment, false);
        } catch (AppointmentException e) {
            throw new AppointmentException("Is conflictive!");
        }

        rejectAllConflictingAppointments(appointment);
        appointment.setAppointmentStatus(Appointment.AppointmentStatus.ACCEPTED);
        appointment.setConfirmationNotes(professionalComment);
        appointmentRepository.save(appointment);
    }

    @Override
    public void rejectAppointment(User user, String appointmentId, String professionalComment) throws AppointmentException {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment == null) throw new AppointmentException("Appointment not found");

        if (appointment.getAppointmentStatus() == Appointment.AppointmentStatus.CANCELLED_BY_USER ||
                appointment.getAppointmentStatus() == Appointment.AppointmentStatus.CANCELLED_BY_PROFESSIONAL)
            throw new AppointmentException("Appointment is already rejected");

        if (!Objects.equals(appointment.getUser().getId(), user.getId()))
            throw new AppointmentException("User does not have permission to reject this appointment");

        appointment.setAppointmentStatus(Appointment.AppointmentStatus.CANCELLED_BY_USER);
        appointment.setCancellationReason(professionalComment);
        appointmentRepository.save(appointment);
    }

    @Override
    public void rejectAppointment(Professional professional, String appointmentId, String professionalComment) throws AppointmentException {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment == null) throw new AppointmentException("Appointment not found");

        if (appointment.getAppointmentStatus() == Appointment.AppointmentStatus.CANCELLED_BY_PROFESSIONAL ||
                appointment.getAppointmentStatus() == Appointment.AppointmentStatus.CANCELLED_BY_USER)
            throw new AppointmentException("Appointment is already rejected");

        if (!Objects.equals(appointment.getProfessional().getId(), professional.getId()))
            throw new AppointmentException("Professional does not have permission to reject this appointment");

        appointment.setAppointmentStatus(Appointment.AppointmentStatus.CANCELLED_BY_PROFESSIONAL);
        appointment.setCancellationReason(professionalComment);
        appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getAppointmentsByStatus(User user, String status) {
        return switch (status) {
            case "pending" -> appointmentRepository.findPendingAppointmentByUserId(user.getId());
            case "accepted" -> appointmentRepository.findAcceptedAppointmentByUserId(user.getId());
            case "cancelled" -> appointmentRepository.findRejectedAppointmentByUserId(user.getId());
            case "completed" -> appointmentRepository.findCompletedAppointmentByUserId(user.getId());
            case "all" -> appointmentRepository.findByUserId(user.getId());
            default -> null;
        };
    }

    @Override
    public List<Appointment> getAppointmentsByStatus(Professional professional, String status) {
        return switch (status) {
            case "pending" -> appointmentRepository.findPendingAppointmentByProfessionalId(professional.getId());
            case "accepted" -> appointmentRepository.findAcceptedAppointmentByProfessionalId(professional.getId());
            case "cancelled" -> appointmentRepository.findRejectedAppointmentByProfessionalId(professional.getId());
            case "completed" -> appointmentRepository.findCompletedAppointmentByProfessionalId(professional.getId());
            case "all" -> appointmentRepository.findByProfessionalId(professional.getId());
            default -> null;
        };
    }

    @Override
    public Appointment getAppointmentById(User user, String appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment == null) return null;
        if (!Objects.equals(appointment.getUser().getId(), user.getId())) return null;
        return appointment;
    }

    @Override
    public Appointment getAppointmentById(Professional professional, String appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment == null) return null;
        if (!Objects.equals(appointment.getProfessional().getId(), professional.getId())) return null;
        return appointment;
    }

    @Transactional
    @Override
    public void markAsCompleted(Professional professional, String appointmentId, Diagnosis diagnosis) throws AppointmentException {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment == null) throw new AppointmentException("Appointment not found");

        if (appointment.getAppointmentStatus() != Appointment.AppointmentStatus.ACCEPTED)
            throw new AppointmentException("Appointment is not accepted");

        if (!Objects.equals(appointment.getProfessional().getId(), professional.getId()))
            throw new AppointmentException("Professional does not have permission to mark this appointment as completed");


        if (
                diagnosis == null ||
                        diagnosis.getHeader() == null || diagnosis.getHeader().isBlank() ||
                        diagnosis.getShortDescription() == null || diagnosis.getShortDescription().isBlank()
        ) {
            throw new AppointmentException("Invalid diagnosis. Basic fields are required and must not exceed 255 characters");
        }

        appointment.setAppointmentStatus(Appointment.AppointmentStatus.COMPLETED);
        appointment.setDiagnosis(diagnosis);
        appointmentRepository.save(appointment);

        diagnosis.setAppointment(appointment);
        diagnosisRepository.save(diagnosis);
    }
}
