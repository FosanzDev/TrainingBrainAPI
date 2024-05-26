package com.fosanzdev.trainingBrainAPI.controllers.appointments;

import com.fosanzdev.trainingBrainAPI.models.data.Opinion;
import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.models.data.User;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IOpinionService;
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

import java.util.Map;

@RestController
@RequestMapping("/opinions")
@Tag(name = "Opinions", description = "Controlador de opiniones")
public class OpinionController {

    @Autowired
    private IOpinionService opinionService;

    @Autowired
    private IUserDataService userDataService;

    @Autowired
    private IProDataService professionalRepository;

    @Autowired
    private IProDataService proDataService;

    @Operation(summary = "Nueva opinión", description = "Crea una nueva opinión sobre un profesional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Opinión creada",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "message": "Opinion created"
                        }
                    """))),
            @ApiResponse(responseCode = "400", description = "Petición incorrecta",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "message": "Bad request / Opinion malformed or repeated"
                        }
                    """))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "message": "Unauthorized"
                        }
                    """))),

            @ApiResponse(responseCode = "404", description = "Profesional no encontrado",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "message": "Professional not found"
                        }
                    """)))
    })
    @PostMapping("/create/{professionalId}")
    ResponseEntity<Map<String, Object>> createOpinion(
            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer,

            @Parameter(description = "Datos de la opinión", required = true, example = """
                {
                    "title": "Excelente profesional",
                    "description": "Muy buen trato y atención",
                    "rating": 10
                }
            """)
            @RequestBody Map<String, Object> jsonData,

            @Parameter(description = "ID del profesional", required = true, example = "daskc-23cas-23cas-23cas")
            @PathVariable String professionalId
    ) {
        try {
            Professional professional = professionalRepository.getProfessionalById(professionalId);
            if (professional == null)
                return ResponseEntity.status(404).body(Map.of("message", "Professional not found"));

            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));

            Opinion opinion = Opinion.fromMap(jsonData);
            if (opinion == null) return ResponseEntity.status(400).body(Map.of("message", "Bad request"));

            if (opinionService.createOpinion(user, professional, opinion))
                return ResponseEntity.status(201).body(Map.of("message", "Opinion created"));
            else return ResponseEntity.status(400).body(Map.of("message", "Opinion malformed or repeated"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    @Operation(summary = "Eliminar opinión", description = "Elimina una opinión")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Opinión eliminada",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "message": "Opinion deleted"
                        }
                    """))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "message": "Unauthorized"
                        }
                    """))),
            @ApiResponse(responseCode = "404", description = "Opinión no encontrada",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "message": "Opinion not found"
                        }
                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "message": "Internal server error"
                        }
                    """)))
    })
    @DeleteMapping("/delete/{id}")
    ResponseEntity<Map<String, Object>> deleteOpinion(
            @Parameter(description = "Token de autorización", required = true, example="Bearer <token>")
            @RequestHeader("Authorization") String bearer,

            @Parameter(description = "ID de la opinión", required = true, example = "daskc-23cas-23cas-23cas")
            @PathVariable String id
    ) {
        try {
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));

            if (opinionService.deleteOpinion(user, id))
                return ResponseEntity.status(200).body(Map.of("message", "Opinion deleted"));
            else return ResponseEntity.status(404).body(Map.of("message", "Opinion not found"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    @Operation(summary = "Mis opiniones", description = "Obtiene las opiniones del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Opiniones obtenidas",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "opinions": [
                                {
                                    "id": "daskc-23cas-23cas-23cas",
                                    "title": "Excelente profesional",
                                    "description": "Muy buen trato y atención",
                                    "rating": 10
                                },
                                {
                                    "id": "daskc-23cas-23cas-23cas",
                                    "title": "Excelente profesional",
                                    "description": "Muy buen trato y atención",
                                    "rating": 10
                                }
                            ]
                        }
                    """))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "message": "Unauthorized"
                        }
                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "message": "Internal server error"
                        }
                    """)))
    })
    @GetMapping("/my")
    ResponseEntity<Map<String, Object>> getMyOpinions(
            @Parameter(description = "Token de autorización", required = true, example="Bearer <token>")
            @RequestHeader("Authorization") String bearer
    ) {
        try {
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            Professional professional = proDataService.getProfessionalByAccessToken(token);

            if (user == null){
                if (professional == null) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
                else {
                    return ResponseEntity.status(200).body(Map.of("opinions", opinionService.
                            getMyOpinions(professional).
                            stream().map(Opinion::toMap)));
                }
            } else {
                return ResponseEntity.status(200).body(Map.of("opinions", opinionService.
                        getMyOpinions(user).
                        stream().map(Opinion::toMap)));
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    @Operation(summary = "Opiniones por profesional", description = "Obtiene las opiniones de un profesional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Opiniones obtenidas",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "opinions": [
                                {
                                    "id": "daskc-23cas-23cas-23cas",
                                    "title": "Excelente profesional",
                                    "description": "Muy buen trato y atención",
                                    "rating": 10
                                },
                                {
                                    "id": "daskc-23cas-23cas-23cas",
                                    "title": "Excelente profesional",
                                    "description": "Muy buen trato y atención",
                                    "rating": 10
                                }
                            ]
                        }
                    """))),
            @ApiResponse(responseCode = "404", description = "Profesional no encontrado",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "message": "Professional not found"
                        }
                    """))),
            @ApiResponse(responseCode = "500", description = "Error interno",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "message": "Internal server error"
                        }
                    """)))
    })
    @GetMapping("/professional/{professionalId}")
    ResponseEntity<Map<String, Object>> getOpinionsByProfessional(
            @Parameter(description = "ID del profesional", required = true, example = "daskc-23cas-23cas-23cas")
            @PathVariable String professionalId
    ) {
        try {
            Professional professional = professionalRepository.getProfessionalById(professionalId);
            if (professional == null)
                return ResponseEntity.status(404).body(Map.of("message", "Professional not found"));

            return ResponseEntity.status(200).body(Map.of("opinions", opinionService.
                    getOpinionsByProfessional(professional).
                    stream().map(Opinion::toMap)));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }
}
