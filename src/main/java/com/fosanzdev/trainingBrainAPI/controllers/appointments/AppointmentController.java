package com.fosanzdev.trainingBrainAPI.controllers.appointments;

import com.fosanzdev.trainingBrainAPI.models.appointments.Appointment;
import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.models.details.User;
import com.fosanzdev.trainingBrainAPI.services.appointments.AppointmentException;
import com.fosanzdev.trainingBrainAPI.services.interfaces.appointments.IAppointmentsService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IProDataService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IUserDataService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
@Tag(name = "Appointments", description = "Endpoint para gestión de citas")
public class AppointmentController {

    @Autowired
    private IUserDataService userDataService;

    @Autowired
    private IProDataService proDataService;

    @Autowired
    private IAppointmentsService appointmentsService;

    @PostMapping("/request/{professionalId}")
    ResponseEntity<Map<String, Object>> applyAppointment(
            @PathVariable String professionalId,
            @RequestBody Map<String, Object> body,
            @RequestHeader("Authorization") String bearer
    ) {
        String token = bearer.split(" ")[1];
        User user = userDataService.getUserByAccessToken(token);
        if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

        Professional professional = proDataService.getProfessionalById(professionalId);
        if (professional == null) return ResponseEntity.status(400).body(Map.of("message", "Invalid professional"));

        Appointment appointment = Appointment.fromMap(body);
        appointment.setUser(user);
        appointment.setProfessional(professional);
        try {
            appointmentsService.bookAppointment(appointment);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }

        return ResponseEntity.ok(Map.of("message", "Appointment requested"));
    }

    @GetMapping("/status/{status}")
    ResponseEntity<Map<String, Object>> getAppointmentsByType(
            @PathVariable String status,
            @RequestHeader("Authorization") String bearer
    ) {
        String token = bearer.split(" ")[1];
        User user = userDataService.getUserByAccessToken(token);
        Professional professional = proDataService.getProfessionalByAccessToken(token);

        if (user == null && professional == null)
            return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

        List<Appointment> appointments = null;

        if (professional != null)
            appointments = appointmentsService.getAppointmentsByStatus(professional, status);
        else
            appointments = appointmentsService.getAppointmentsByStatus(user, status);

        if (appointments == null) return ResponseEntity.status(404).body(Map.of("error", "Invalid status: " + status));

        List<Map<String, Object>> appointmentsMap = appointments.stream().map(Appointment::toMap).toList();
        return ResponseEntity.ok(Map.of("appointments", appointmentsMap));
    }

    @PostMapping("/accept/{appointmentId}")
    ResponseEntity<Map<String, Object>> acceptAppointment(
            @PathVariable String appointmentId,
            @RequestBody Map<String, Object> body,
            @RequestHeader("Authorization") String bearer
    ) {
        try {
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null) return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

            appointmentsService.acceptAppointment(appointmentId, (String) body.get("professionalComment"));

            return ResponseEntity.ok(Map.of("message", "Appointment accepted"));

        } catch (AppointmentException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/cancel/{appointmentId}")
    ResponseEntity<Map<String, Object>> rejectAppointment(
            @PathVariable String appointmentId,
            @RequestBody Map<String, Object> body,
            @RequestHeader("Authorization") String bearer
    ) {
        try {
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            User user = userDataService.getUserByAccessToken(token);

            if (professional == null){
                //User is cancelling the appointment
                if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));
                appointmentsService.rejectAppointment(user, appointmentId, (String) body.get("comment"));
            }

            //Professional is rejecting the appointment
            else appointmentsService.rejectAppointment(professional, appointmentId, (String) body.get("comment"));


            return ResponseEntity.ok(Map.of("message", "Appointment cancelled"));

        } catch (AppointmentException e) {
            return ResponseEntity.status(400).body(Map.of("error", e));
        }
    }

    @GetMapping("/{appointmentId}")
    ResponseEntity<Map<String, Object>> getAppointment(
            @PathVariable String appointmentId,
            @RequestHeader("Authorization") String bearer
    ) {
        String token = bearer.split(" ")[1];
        User user = userDataService.getUserByAccessToken(token);
        Professional professional = proDataService.getProfessionalByAccessToken(token);

        if (user == null && professional == null)
            return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

        Appointment appointment = null;

        if (professional != null)
            appointment = appointmentsService.getAppointmentById(professional, appointmentId);
        else
            appointment = appointmentsService.getAppointmentById(user, appointmentId);

        if (appointment == null) return ResponseEntity.status(404).body(Map.of("error", "Invalid appointment id"));

        return ResponseEntity.ok().body(Map.of("appointment", appointment.toMap()));
    }
}

