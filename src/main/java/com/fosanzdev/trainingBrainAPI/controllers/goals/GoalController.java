package com.fosanzdev.trainingBrainAPI.controllers.goals;

import com.fosanzdev.trainingBrainAPI.models.details.User;
import com.fosanzdev.trainingBrainAPI.models.goals.Goal;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IUserDataService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.goals.IGoalService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/goals")
@RestController
@Tag(name = "Goals", description = "Controlador de metas")
public class GoalController {

    @Autowired
    private IGoalService goalService;

    @Autowired
    private IUserDataService userDataService;

    @Operation(summary = "Agregar meta", description = "Agrega una meta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meta agregada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Goal added"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Meta inválida",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Invalid goal"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Token inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Invalid token"
                                    }
                                    """)))
    })
    @PostMapping("/add")
    ResponseEntity<Map<String, Object>> addGoal(
            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer,

            @Parameter(description = """
                    Datos de la meta. Cada meta se repite cada ciertas horas y cierto número de veces.
                    Se debe especificar la fecha y hora de inicio de la meta. La fecha y hora de comienzo
                    no puede superar ni las 12 horas antes de la fecha y hora actual ni los 7 días más tarde.
                    """, required = true,
                    schema = @Schema(example = """
                                    {
                                        "title": "Meta 1",
                                        "description": "Descripción de la meta",
                                        "hoursBetween": 2,
                                        "repetitions": 3,
                                        "startDateTime": "2021-09-01T00:00:00Z"
                                    }
                                    """))
            @RequestBody Map<String, Object> body
    ) {
        try {
            String token = bearer.split(" ")[1]; // Bearer token
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

            Goal goal = Goal.fromMap(body);
            if (goal == null) return ResponseEntity.status(400).body(Map.of("message", "Goal malformed"));

            if (goalService.addGoal(goal, user)) {
                return ResponseEntity.ok(Map.of("message", "Goal added"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid goal"));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Marcar meta como completada", description = "Marca una meta como completada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meta marcada como completada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Goal marked as done"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Meta ya completada o ya hecha hoy",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Goal already completed or done today"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Token inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Invalid token"
                                    }
                                    """)))
    })
    @PostMapping("/done/{id}")
    ResponseEntity<Map<String, Object>> markGoalAsDone(
            @Parameter(description = "Token de autorización", required = true, example= "Bearer <token>")
            @RequestHeader("Authorization") String bearer,

            @Parameter(description = "ID de la meta", required = true, example = "1fsd-2fsd-3fsd")
            @PathVariable String id
    ) {
        try {
            String token = bearer.split(" ")[1]; // Bearer token
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

            Goal goal = goalService.getGoal(user, id);
            if (goal == null) return ResponseEntity.badRequest().body(Map.of("message", "No goal found"));

            boolean result = goalService.markGoalAsDone(goal);
            if (!result)
                return ResponseEntity.badRequest().body(Map.of("message", "Goal already completed or done today"));

            return ResponseEntity.ok().body(Map.of("message", "Goal marked as done"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Obtener metas de hoy", description = "Obtiene las metas por completar hoy")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metas obtenidas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "goals": [
                                            {
                                                "id": "1fsd-2fsd-3fsd",
                                                "title": "Meta 1",
                                                "description": "Descripción de la meta",
                                                "completed": false,
                                                "hoursBetween": 2,
                                                "repetitions": 3,
                                                "startDateTime": "2021-09-01T00:00:00Z",
                                                "pendingToday": true
                                            }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "No se encontraron metas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "No goals found"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Token inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Invalid token"
                                    }
                                    """)))
    })
    @GetMapping("/today")
    ResponseEntity<Map<String, Object>> getTodayGoals(
            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer
    ) {
        try {
            String token = bearer.split(" ")[1]; // Bearer token
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

            List<Goal> goals = goalService.getAllGoals(user);
            if (goals == null) return ResponseEntity.badRequest().body(Map.of("message", "No goals found"));

            List<Map<String, Object>> goalsMap = new ArrayList<>();
            for (Goal goal : goals) {
                Map<String, Object> goalMap = new HashMap<>(goal.toMap());
                if (goalService.pendingToday(goal)) {
                    goalMap.put("pendingToday", true);
                    goalsMap.add(goalMap);
                }
            }


            return ResponseEntity.ok().body(Map.of("goals", goalsMap));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @Operation(summary = "Obtener meta", description = "Obtiene una meta por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meta obtenida",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "id": "1fsd-2fsd-3fsd",
                                        "title": "Meta 1",
                                        "description": "Descripción de la meta",
                                        "completed": false,
                                        "hoursBetween": 2,
                                        "repetitions": 3,
                                        "startDateTime": "2021-09-01T00:00:00Z",
                                        "pendingToday": true
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "No se encontró la meta",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "No goal found"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Token inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Invalid token"
                                    }
                                    """)))
    })
    @GetMapping("/get/{id}")
    ResponseEntity<Map<String, Object>> getGoal(
            @Parameter(description = "Token de autorización", required = true, example= "Bearer <token>")
            @RequestHeader("Authorization") String bearer,

            @Parameter(description = "ID de la meta", required = true, example = "1fsd-2fsd-3fsd")
            @PathVariable String id
    ) {
        try {
            String token = bearer.split(" ")[1]; // Bearer token
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

            Goal goal = goalService.getGoal(user, id);
            if (goal == null) return ResponseEntity.badRequest().body(Map.of("message", "No goal found"));

            Map<String, Object> goalMap = new HashMap<>(goal.toMap());
            goalMap.put("pendingToday", goalService.pendingToday(goal));

            return ResponseEntity.ok().body(goalMap);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Obtener todas las metas", description = "Obtiene todas las metas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metas obtenidas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "goals": [
                                            {
                                                "id": "1fsd-2fsd-3fsd",
                                                "title": "Meta 1",
                                                "description": "Descripción de la meta",
                                                "completed": false,
                                                "hoursBetween": 2,
                                                "repetitions": 3,
                                                "startDateTime": "2021-09-01T00:00:00Z",
                                                "pendingToday": true
                                            }
                                        ]
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "No se encontraron metas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "No goals found"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Token inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Invalid token"
                                    }
                                    """)))
    })
    @GetMapping("/all")
    ResponseEntity<Map<String, Object>> getAllGoals(
            @Parameter(description = "Token de autorización", required = true, example= "Bearer <token>")
            @RequestHeader("Authorization") String bearer
    ) {
        try {
            String token = bearer.split(" ")[1]; // Bearer token
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

            List<Goal> goals = goalService.getAllGoals(user);
            if (goals == null) return ResponseEntity.badRequest().body(Map.of("message", "No goals found"));


            List<Map<String, Object>> goalsMap = new ArrayList<>();
            for (Goal goal : goals) {
                Map<String, Object> goalMap = new HashMap<>(goal.toMap());
                goalMap.put("pendingToday", goalService.pendingToday(goal));
                goalsMap.add(goalMap);
            }

            return ResponseEntity.ok().body(Map.of("goals", goalsMap));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @Operation(summary = "Eliminar meta", description = "Elimina una meta por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meta eliminada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Goal deleted"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "No se encontró la meta",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "No goal found"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Token inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "message": "Invalid token"
                                    }
                                    """)))
    })
    @DeleteMapping("/delete/{id}")
    ResponseEntity<Map<String, Object>> deleteGoal(
            @RequestHeader("Authorization") String bearer,
            @PathVariable String id
    ) {
        try {
            String token = bearer.split(" ")[1]; // Bearer token
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

            boolean result = goalService.deleteGoal(user, id);
            if (!result) return ResponseEntity.badRequest().body(Map.of("message", "No goal found"));

            return ResponseEntity.ok().body(Map.of("message", "Goal deleted"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
