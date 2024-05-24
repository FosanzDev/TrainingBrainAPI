package com.fosanzdev.trainingBrainAPI.controllers.appointments;

import com.fosanzdev.trainingBrainAPI.models.appointments.Appointment;
import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.models.details.User;
import com.fosanzdev.trainingBrainAPI.services.data.UserDataService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.appointments.IAppointmentsService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IProDataService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IUserDataService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        try{
            appointmentsService.bookAppointment(appointment);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }

        return ResponseEntity.ok(Map.of("message", "Appointment requested"));
    }
}
