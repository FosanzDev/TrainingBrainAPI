package com.fosanzdev.trainingBrainAPI.controllers.goals;

import com.fosanzdev.trainingBrainAPI.models.details.User;
import com.fosanzdev.trainingBrainAPI.models.goals.Routine;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IUserDataService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.goals.IRoutineService;
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

@RequestMapping("/routines")
@RestController
@Tag(name = "Routine", description = "Manejo de rutinas")
public class RoutineController {

    @Autowired
    private IRoutineService routineService;

    @Autowired
    private IUserDataService userDataService;

    @Operation(summary = "Añade una nueva rutina", description = "Añade una nueva rutina al usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Routine added",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Routine added"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Routine malformed",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Routine malformed"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Unauthorized"
                                    }
                                    """))),
    })
    @PostMapping("/add")
    ResponseEntity<Map<String, Object>> addRoutine(
            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer,

            @Parameter(description = "Datos de la rutina", required = true,
                    schema = @Schema(example = """
                            {
                                "title": "Rutina de ejercicios",
                                "description": "Rutina de ejercicios para mantenerse en forma",
                                "every": 1,
                                "routineType": "DAYS",
                                "startDateTime": "2021-09-01T00:00:00Z",
                                "endDateTime": "2021-09-30T00:00:00Z"
                            }
                            """))
            @RequestBody Map<String, Object> body
    ) {
        try {
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));

            Routine routine = Routine.fromMap(body);
            if (routine == null) return ResponseEntity.status(400).body(Map.of("message", "Routine malformed"));

            if (routineService.addRoutine(routine, user)) {
                return ResponseEntity.status(201).body(Map.of("message", "Routine added"));
            } else {
                return ResponseEntity.status(500).body(Map.of("message", "Error adding routine"));
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return ResponseEntity.status(500).body(Map.of("message", "Error adding routine"));
        }
    }

    @Operation(summary = "Lista de rutinas para hoy", description = "Lista las rutinas en un marco de 24 horas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rutinas listadas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "routines": [
                                            {
                                                "id": "1",
                                                "title": "Rutina de ejercicios",
                                                "description": "Rutina de ejercicios para mantenerse en forma",
                                                "every": 1,
                                                "routineType": "DAYS",
                                                "startTime": "2021-09-01T00:00:00Z",
                                                "endTime": "2021-09-30T00:00:00Z",
                                                "submissionDate": "2021-09-01T00:00:00Z"
                                            }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Unauthorized"
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error getting routines",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Error getting routines"
                                    }
                                    """))),
    })
    @GetMapping("/today")
    ResponseEntity<Map<String, Object>> getRoutinesToday(
            @Parameter(description = "Token de autorización", required = true, example="Bearer <token>")
            @RequestHeader("Authorization") String bearer
    ) {
        try {
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));

            List<Routine> routines = routineService.getTodayRoutines(user);
            if (routines == null) return ResponseEntity.status(500).body(Map.of("message", "Error getting routines"));

            return ResponseEntity.status(200).body(Map.of("routines", routines.stream().map(Routine::toMap)));
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return ResponseEntity.status(500).body(Map.of("message", "Error getting routines"));
        }
    }


    @Operation(summary = "Obtener una rutina", description = "Obtiene una rutina por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rutina obtenida",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "id": "1",
                                        "title": "Rutina de ejercicios",
                                        "description": "Rutina de ejercicios para mantenerse en forma",
                                        "every": 1,
                                        "routineType": "DAYS",
                                        "startTime": "2021-09-01T00:00:00Z",
                                        "endTime": "2021-09-30T00:00:00Z",
                                        "submissionDate": "2021-09-01T00:00:00Z"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Unauthorized"
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Rutina no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Routine not found"
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error getting routine",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Error getting routine"
                                    }
                                    """))),
    })
    @GetMapping("/get/{id}")
    ResponseEntity<Map<String, Object>> getRoutine(
            @RequestHeader("Authorization") String bearer,
            @PathVariable String id
    ) {
        try {
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));

            Routine routine = routineService.getRoutine(user, id);
            if (routine == null) return ResponseEntity.status(404).body(Map.of("message", "Routine not found"));

            return ResponseEntity.ok(routine.toMap());
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return ResponseEntity.status(500).body(Map.of("message", "Error getting routine"));
        }
    }

    @Operation(summary = "Obtener todas las rutinas", description = "Obtiene todas las rutinas del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rutinas obtenidas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "routines": [
                                            {
                                                "id": "1",
                                                "title": "Rutina de ejercicios",
                                                "description": "Rutina de ejercicios para mantenerse en forma",
                                                "every": 1,
                                                "routineType": "DAYS",
                                                "startTime": "2021-09-01T00:00:00Z",
                                                "endTime": "2021-09-30T00:00:00Z",
                                                "submissionDate": "2021-09-01T00:00:00Z"
                                            }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Unauthorized"
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error getting routines",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Error getting routines"
                                    }
                                    """))),
    })
    @GetMapping("/all")
    ResponseEntity<Map<String, Object>> getAllRoutines(
            @RequestHeader("Authorization") String bearer
    ) {
        try {
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));

            List<Routine> routines = routineService.getAllRoutines(user);
            if (routines == null) return ResponseEntity.status(500).body(Map.of("message", "Error getting routines"));

            return ResponseEntity.status(200).body(Map.of("routines", routines.stream().map(Routine::toMap)));
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return ResponseEntity.status(500).body(Map.of("message", "Error getting routines"));
        }
    }

    @Operation(summary = "Eliminar una rutina", description = "Elimina una rutina por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rutina eliminada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Routine deleted"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Unauthorized"
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Rutina no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Routine not found"
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Error deleting routine",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Error deleting routine"
                                    }
                                    """))),
    })
    @DeleteMapping("/delete/{id}")
    ResponseEntity<Map<String, Object>> deleteRoutine(
            @RequestHeader("Authorization") String bearer,
            @PathVariable String id
    ) {
        try {
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));

            boolean result = routineService.deleteRoutine(user, id);
            if (result) return ResponseEntity.status(200).body(Map.of("message", "Routine deleted"));
            else return ResponseEntity.status(404).body(Map.of("message", "routine not found"));

        } catch (Exception e) {
            e.printStackTrace(System.out);
            return ResponseEntity.status(500).body(Map.of("message", "Error deleting routine"));
        }
    }
}


