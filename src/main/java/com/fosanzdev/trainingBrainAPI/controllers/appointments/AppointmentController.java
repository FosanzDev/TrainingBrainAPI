package com.fosanzdev.trainingBrainAPI.controllers.appointments;

import com.fosanzdev.trainingBrainAPI.models.appointments.Appointment;
import com.fosanzdev.trainingBrainAPI.models.appointments.Diagnosis;
import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.models.details.User;
import com.fosanzdev.trainingBrainAPI.services.appointments.AppointmentException;
import com.fosanzdev.trainingBrainAPI.services.interfaces.appointments.IAppointmentsService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IProDataService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IUserDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Solicitar cita", description = "Solicitar una cita con un profesional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cita solicitada", content = @Content(mediaType = "none")),
            @ApiResponse(responseCode = "400", description = "Error al solicitar la cita",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{error: 'Error message'}"))),
            @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(mediaType = "none"))
    })
    @PostMapping("/request/{professionalId}")
    ResponseEntity<Map<String, Object>> applyAppointment(
            @Parameter(description = "ID del profesional")
            @PathVariable String professionalId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la cita",
                    content = @Content(schema = @Schema(example = """
                            {
                                "startDateTime": "2021-09-01T10:00:00Z",
                                "endDateTime": "2021-09-01T11:00:00Z",
                                "submissionNotes": "Nota para el profesional"
                            }
                                                        
                            """)))
            @RequestBody Map<String, Object> body,

            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
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

    @Operation(summary = "Listar citas", description = "Listar citas por estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Citas listadas", content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                            {
                                "appointments": [
                                    {
                                        "id": "1",
                                        "startDateTime": "2024-09-01T10:00:00Z",
                                        "endDateTime": "2024-09-01T11:00:00Z",
                                        "submissionTime": "2021-08-01T10:00:00Z",
                                        "submissionNotes": "Nota para el profesional",
                                        "cancellationReason": "Razón de cancelación",
                                        "confirmationNotes": "Notas de confirmación",
                                        "professional": "<professional_id>",
                                        "user": "<user_id>",
                                        "appointmentStatus": "PENDING/ACCEPTED/CANCELLED_BY_PROFESSIONAL/CANCELLED_BY_USER/COMPLETED",
                                    }
                                ]
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(mediaType = "none")),
            @ApiResponse(responseCode = "404", description = "Estado inválido", content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{error: 'Invalid status'}")))
    })
    @GetMapping("/status/{status}")
    ResponseEntity<Map<String, Object>> getAppointmentsByType(
            @Parameter(description = "Estado de la cita", required = true, example = "pending/accepted/cancelled/completed/all")
            @PathVariable String status,

            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
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


    @Operation(summary = "Aceptar cita", description = "Aceptar una cita")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cita aceptada", content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{message: 'Appointment accepted'}"))),
            @ApiResponse(responseCode = "400", description = "Error al aceptar la cita",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{error: 'Error message'}"))),
            @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(mediaType = "none"))
    })
    @PostMapping("/accept/{appointmentId}")
    ResponseEntity<Map<String, Object>> acceptAppointment(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable String appointmentId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Comentario del profesional",
                    content = @Content(schema = @Schema(example = """
                            {
                                "comment": "Comentario del profesional"
                            }
                            """)))
            @RequestBody Map<String, Object> body,

            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer
    ) {
        try {
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null) return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

            appointmentsService.acceptAppointment(appointmentId, (String) body.get("comment"));

            return ResponseEntity.ok(Map.of("message", "Appointment accepted"));

        } catch (AppointmentException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }


    @Operation(summary = "Cancelar cita", description = "Cancela una cita")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cita cancelada", content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{message: 'Appointment cancelled'}"))),
            @ApiResponse(responseCode = "400", description = "Error al cancelar la cita",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{error: 'Error message'}"))),
            @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(mediaType = "none"))
    })
    @PostMapping("/cancel/{appointmentId}")
    ResponseEntity<Map<String, Object>> rejectAppointment(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable String appointmentId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Comentario del profesional",
                    content = @Content(schema = @Schema(example = """
                            {
                                "comment": "Motivo de la cancelación"
                            }
                            """)))
            @RequestBody Map<String, Object> body,

            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer
    ) {
        try {
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            User user = userDataService.getUserByAccessToken(token);

            if (professional == null) {
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


    @Operation(summary = "Obtener cita por ID", description = "Obtener información de una cita por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cita encontrada", content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                            {
                                "appointment": {
                                    "id": "1",
                                    "startDateTime": "2024-09-01T10:00:00Z",
                                    "endDateTime": "2024-09-01T11:00:00Z",
                                    "submissionTime": "2021-08-01T10:00:00Z",
                                    "submissionNotes": "Nota para el profesional",
                                    "cancellationReason": "Razón de cancelación",
                                    "confirmationNotes": "Notas de confirmación",
                                    "professional": "<professional_id>",
                                    "user": "<user_id>",
                                    "appointmentStatus": "PENDING/ACCEPTED/CANCELLED_BY_PROFESSIONAL/CANCELLED_BY_USER/COMPLETED",
                                    "diagnosis": {
                                        "header": "Diagnóstico",
                                        "shortDescription": "Descripción corta",
                                        "description": "Descripción",
                                        "recommendation": "Recomendación",
                                        "treatment": "Tratamiento"
                                    }
                                }
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(mediaType = "none")),
            @ApiResponse(responseCode = "404", description = "Cita no encontrada", content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{error: 'Invalid appointment id'}")))
    })
    @GetMapping("/{appointmentId}")
    ResponseEntity<Map<String, Object>> getAppointment(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable String appointmentId,

            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer
    ) {
        String token = bearer.split(" ")[1];
        User user = userDataService.getUserByAccessToken(token);
        Professional professional = proDataService.getProfessionalByAccessToken(token);

        if (user == null && professional == null)
            return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

        Appointment appointment;

        if (professional != null)
            appointment = appointmentsService.getAppointmentById(professional, appointmentId);
        else
            appointment = appointmentsService.getAppointmentById(user, appointmentId);

        if (appointment == null) return ResponseEntity.status(404).body(Map.of("error", "Invalid appointment id"));

        return ResponseEntity.ok().body(Map.of("appointment", appointment.toMap()));
    }

    @Operation(summary = "Marcar cita como completada", description = "Marcar una cita como completada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cita completada", content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{message: 'Appointment completed'}"))),
            @ApiResponse(responseCode = "400", description = "Error al completar la cita",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{error: 'Error message'}"))),
            @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(mediaType = "none"))
    })
    @PostMapping("/complete/{appointmentId}")
    ResponseEntity<Map<String, Object>> markAsCompleted(
            @PathVariable String appointmentId,
            @RequestHeader("Authorization") String bearer,
            @RequestBody Map<String, Object> body
    ) {
        try {
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null) return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

            Diagnosis diagnosis = Diagnosis.fromMap(body);
            try {
                appointmentsService.markAsCompleted(professional, appointmentId, diagnosis);
            } catch (AppointmentException e) {
                return ResponseEntity.status(400).body(Map.of("error", e.toString()));
            }

            return ResponseEntity.ok(Map.of("message", "Appointment completed"));

        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", "Bad request"));
        }
    }
}

