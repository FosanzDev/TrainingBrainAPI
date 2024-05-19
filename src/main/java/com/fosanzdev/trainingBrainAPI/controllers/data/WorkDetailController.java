package com.fosanzdev.trainingBrainAPI.controllers.data;

import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.models.details.WorkDetail;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IProDataService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IWorkDetailService;
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

@RequestMapping("/pro/workdetails")
@RestController
@Tag(name = "Work Details", description = "Controlador de detalles de trabajo")
public class WorkDetailController {

    @Autowired
    private IWorkDetailService workDetailService;

    @Autowired
    private IProDataService proDataService;


    @Operation(summary = "Obtiene el historial de trabajo del profesional actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de trabajo",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "workDetails": [
                                            {
                                                "id": 1,
                                                "workTitle": {"...":"..."},
                                                "company": "Empresa",
                                                "startDate": "2020/01",
                                                "endDate": "2021/01",
                                                "description": "Descripción"
                                            }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Unauthorized\"}")))
    })
    @GetMapping("/me")
    ResponseEntity<Map<String, Object>> getMyWorkHistory(
            @Parameter(description = "Token de autenticación", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer
    ) {
        try {
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);

            if (professional == null)
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

            List<WorkDetail> workHistory = professional.getWorkDetails();
            return ResponseEntity.ok(Map.of("workDetails", workHistory));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }

    }

    @Operation(summary = "Añade un detalle de trabajo al historial del profesional actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle de trabajo añadido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\":\"Work detail added\"}"))),
            @ApiResponse(responseCode = "400", description = "Error al añadir el detalle de trabajo",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Error message\"}"))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Unauthorized\"}")))
    })
    @PostMapping("/add")
    ResponseEntity<Map<String, Object>> addWorkDetail(
            @Parameter(description = "Token de autenticación", required = true, example = "Bearer <token")
            @RequestHeader("Authorization") String bearer,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Detalle de trabajo a añadir", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "workTitle": 142434112312312,
                                        "company": "Empresa",
                                        "startDate": "2020/01",
                                        "endDate": "2021/01",
                                        "description": "Descripción"
                                    }
                                    """)))
            @RequestBody Map<String, Object> body
    ) {
        try {
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);

            if (professional == null)
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

            try {
                workDetailService.parseAndAddWorkDetail(professional, body);
                return ResponseEntity.ok(Map.of("message", "Work detail added"));
            } catch (Exception e) {
                return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
            }


        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Elimina un detalle de trabajo del historial del profesional actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle de trabajo eliminado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\":\"Work detail removed\"}"))),
            @ApiResponse(responseCode = "404", description = "Detalle de trabajo no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Work detail not found\"}"))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Unauthorized\"}")))
    })
    @DeleteMapping("/remove/{id}")
    ResponseEntity<Map<String, Object>> removeWorkDetail(
            @Parameter(description = "Token de autenticación", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer,
            @Parameter(description = "ID del detalle de trabajo a eliminar", required = true)
            @PathVariable Long id
    ) {
        try {
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);

            if (professional == null)
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

            try {
                boolean success = workDetailService.removeWorkDetail(professional, id);

                if (!success)
                    return ResponseEntity.status(404).body(Map.of("error", "Work detail not found"));

                return ResponseEntity.ok(Map.of("message", "Work detail removed"));
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Edita un detalle de trabajo del historial del profesional actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle de trabajo editado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\":\"Work detail edited\"}"))),
            @ApiResponse(responseCode = "400", description = "Error al editar el detalle de trabajo",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Error message\"}"))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Unauthorized\"}")))
    })
    @PostMapping("/edit/{id}")
    ResponseEntity<Map<String, Object>> editWorkDetail(
            @Parameter(description = "Token de autenticación", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer,
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Detalle de trabajo a editar", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "workTitle": 142434112312312,
                                        "company": "Empresa",
                                        "startDate": "2020/01",
                                        "endDate": "2021/01",
                                        "description": "Descripción"
                                    }
                                    """)))
            @RequestBody Map<String, Object> body
    ) {
        try {
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);

            if (professional == null)
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

            try {
                workDetailService.parseAndEditWorkDetail(professional, body, id);
                return ResponseEntity.ok(Map.of("message", "Work detail edited"));
            } catch (Exception e) {
                return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
