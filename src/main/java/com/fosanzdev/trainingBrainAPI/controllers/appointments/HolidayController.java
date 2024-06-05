package com.fosanzdev.trainingBrainAPI.controllers.appointments;

import com.fosanzdev.trainingBrainAPI.models.appointments.ProfessionalHoliday;
import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.services.interfaces.appointments.IProHolidaysService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pro/holiday")
@Tag(name = "Professional Holidays", description = "Gestion de vacaciones/bajas de los profesionales")
public class HolidayController {

    @Autowired
    private IProHolidaysService proHolidaysService;

    @Autowired
    private IProDataService proDataService;

    @Operation(summary = "Añadir vacaciones", description = "Añadir una franja de vacaciones al calendario de un profesional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vacaciones añadidas", content = @Content(mediaType = "none")),
            @ApiResponse(responseCode = "400", description = "Petición incorrecta",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"message\": \"Bad request / Invalid holiday data\"}"))),
            @ApiResponse(responseCode = "401", description = "Token inválido",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"message\": \"Invalid token\"}"))),
    })
    @PostMapping("/add")
    ResponseEntity<Map<String, Object>> addHoliday(
            @Parameter(description = "Token de acceso", required = true)
            @RequestHeader("Authorization") String bearer,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de las vacaciones", required = true,
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "holidayType": "VACATION / SICK_LEAVE / OTHER",
                            "description": "Vacaciones de verano",
                            "startDateTime": "2022-07-01T00:00:00Z",
                            "endDateTime": "2022-07-15T23:59:59Z"
                        }
                        """)))
            @RequestBody Map<String, Object> holidayData
    ) {
        try{
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null) return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

            ProfessionalHoliday holiday = ProfessionalHoliday.fromMap(holidayData);
            if (holiday == null) return ResponseEntity.status(400).body(Map.of("message", "Invalid holiday data"));

            if (proHolidaysService.addHoliday(professional, holiday)) return ResponseEntity.ok(Map.of("message", "Holiday added"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(Map.of("message", "Bad request"));
        }

        return ResponseEntity.status(400).body(Map.of("message", "Bad request. Check dates are not overlapping."));
    }

    @Operation(summary = "Eliminar vacaciones", description = "Eliminar una franja de vacaciones del calendario de un profesional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vacaciones eliminadas", content = @Content(mediaType = "application/json",
                schema = @Schema(example = "{\"message\": \"Holiday removed\"}"))),

            @ApiResponse(responseCode = "400", description = "Petición incorrecta",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"message\": \"Bad request / Failed to remove holiday\"}"))),

            @ApiResponse(responseCode = "401", description = "Token inválido",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"message\": \"Invalid token\"}"))),
    })
    @DeleteMapping("/remove/{holidayId}")
    ResponseEntity<Map<String, Object>> removeHoliday(
            @Parameter(description = "ID de las vacaciones", required = true, example = "<holidayId>")
            @PathVariable String holidayId,

            @Parameter(description = "Token de acceso", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer
    ) {
        try{
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null) return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

            if (proHolidaysService.deleteHoliday(professional, holidayId)){
                return  ResponseEntity.ok(Map.of("message", "Holiday removed"));
            } else {
                return ResponseEntity.status(400).body(Map.of("message", "Failed to remove holiday"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", "Bad request"));
        }
    }

    @Operation(summary = "Listar mis vacaciones", description = "Listar las vacaciones de un profesional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vacaciones listadas", content = @Content(mediaType = "application/json",
                schema = @Schema(example = """
                    {
                        "holidays": [
                            {
                                "id": "<holidayId>",
                                "holidayType": "VACATION / SICK_LEAVE / OTHER",
                                "startDateTime": "2022-07-01T00:00:00Z",
                                "endDateTime": "2022-07-15T23:59:59Z",
                                "description": "Vacaciones de verano"
                            }
                        ]
                    }
                    """))),

            @ApiResponse(responseCode = "401", description = "Token inválido",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"message\": \"Invalid token\"}"))),
    })
    @GetMapping("/list/me")
    ResponseEntity<Map<String, Object>> listMyHolidays(
            @Parameter(description = "Token de acceso", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer
    ) {
        String token = bearer.split(" ")[1];
        Professional professional = proDataService.getProfessionalByAccessToken(token);
        if (professional == null) return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

        List<ProfessionalHoliday> holidays = proHolidaysService.findByProfessionalId(professional.getId());
        List<Object> holidaysData = new ArrayList<>();
        for (ProfessionalHoliday holiday : holidays) {
            holidaysData.add(holiday.toMap());
        }
        return ResponseEntity.ok(Map.of("holidays", holidaysData));
    }

    @Operation(summary = "Listar vacaciones de un profesional", description = "Listar las vacaciones de un profesional por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vacaciones listadas", content = @Content(mediaType = "application/json",
                schema = @Schema(example = """
                    {
                        "holidays": [
                            {
                                "holidayType": "VACATION / SICK_LEAVE / OTHER",
                                "startDateTime": "2024-07-01T00:00:00Z",
                                "endDateTime": "2024-07-15T23:59:59Z",
                                "description": "Vacaciones de verano"
                            }
                        ]
                    }
                    """))),

            @ApiResponse(responseCode = "400", description = "Profesional no encontrado",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"message\": \"Professional not found\"}"))),
    })
    @GetMapping("/list/{professionalId}")
    ResponseEntity<Map<String, Object>> listHolidays(
            @Parameter(description = "ID del profesional", required = true, example = "<professionalId>")
            @PathVariable String professionalId
    ) {
        Professional professional = proDataService.getProfessionalById(professionalId);
        if (professional == null) return ResponseEntity.status(400).body(Map.of("message", "Professional not found"));

        List<ProfessionalHoliday> holidays = proHolidaysService.findByProfessionalId(professional.getId());
        List<Object> holidaysData = new ArrayList<>();
        for (ProfessionalHoliday holiday : holidays) {
            holidaysData.add(holiday.toBasicMap());
        }
        return ResponseEntity.ok(Map.of("holidays", holidaysData));
    }
}
