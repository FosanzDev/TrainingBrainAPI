package com.fosanzdev.trainingBrainAPI.controllers.appointments;

import com.fosanzdev.trainingBrainAPI.models.appointments.ProfessionalSchedule;
import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.services.interfaces.appointments.IProScheduleService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IProDataService;
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
@RequestMapping("/pro/schedule")
@Tag(name = "Schedule", description = "Manejo de horarios de los profesionales")
public class ScheduleController {

    @Autowired
    private IProScheduleService proScheduleService;

    @Autowired
    private IProDataService proDataService;

    @Operation(summary = "Modificar horario de un profesional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario modificado",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                          "message": "Schedule updated"
                        }
                        """))),
            @ApiResponse(responseCode = "400", description = "Error al modificar horario",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                          "message": "Failed to update schedule"
                        }
                        """)))
    })
    @PostMapping("/modify")
    ResponseEntity<Map<String, Object>> modifySchedule(
            @Parameter(description = "Token de acceso del profesional", required = true)
            @RequestHeader("Authorization") String bearer,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Horario a modificar",
            content = @Content(mediaType = "application/json",
                schema = @Schema(example = """
                        {
                          "monday": [
                            {
                              "startHour": "09:00",
                              "endHour": "12:00"
                            },\s
                            {
                                "startHour": "13:00",
                                "endHour": "20:00"
                            }
                          ],
                          "tuesday": [
                            {
                              "startHour": "09:00",
                              "endHour": "12:00"
                            }
                          ],
                          "wednesday": [
                            {
                              "startHour": "09:00",
                              "endHour": "12:00"
                            }
                          ],
                          "thursday": [
                            {
                              "startHour": "09:00",
                              "endHour": "12:00"
                            }
                          ],
                          "friday": [
                            {
                              "startHour": "09:00",
                              "endHour": "12:00"
                            }
                          ],
                          "intervalMinutes": 30
                        }
                        """)))
            @RequestBody Map<String, Object> body
    ) {

        try {
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null) return ResponseEntity.badRequest().body(Map.of("message", "Invalid token"));

            List<ProfessionalSchedule> schedules = ProfessionalSchedule.fromNamedDayJsonSchedule(body);
            if (schedules == null) return ResponseEntity.badRequest().body(Map.of("message", "Invalid schedule"));

            if (proScheduleService.changeSchedule(professional, schedules)) {
                return ResponseEntity.ok(Map.of("message", "Schedule updated"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Failed to update schedule"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid request"));
        }
    }

    @Operation(summary = "Obtener horario de un profesional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario obtenido",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                          "monday": [
                            {
                              "startHour": "09:00",
                              "endHour": "12:00"
                            },\s
                            {
                                "startHour": "13:00",
                                "endHour": "20:00"
                            }
                          ],
                          "tuesday": [
                            {
                              "startHour": "09:00",
                              "endHour": "12:00"
                            }
                          ],
                          "wednesday": [
                            {
                              "startHour": "09:00",
                              "endHour": "12:00"
                            }
                          ],
                          "thursday": [
                            {
                              "startHour": "09:00",
                              "endHour": "12:00"
                            }
                          ],
                          "friday": [
                            {
                              "startHour": "09:00",
                              "endHour": "12:00"
                            }
                          ],
                          "intervalMinutes": 30
                        }
                        """))),
            @ApiResponse(responseCode = "400", description = "No se encontró horario",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                          "message": "No schedule found"
                        }
                        """)))
    })
    @GetMapping("/get/{professionalId}")
    ResponseEntity<Map<String, Object>> getSchedule(
            @Parameter(description = "ID del profesional", required = true)
            @PathVariable String professionalId
    ) {
        List<ProfessionalSchedule> schedules = proScheduleService.findByProfessionalId(professionalId);
        if (schedules == null || schedules.isEmpty()) return ResponseEntity.badRequest().body(Map.of("message", "No schedule found"));
        else return ResponseEntity.ok(ProfessionalSchedule.toNamedDayJsonSchedule(schedules));
    }
}