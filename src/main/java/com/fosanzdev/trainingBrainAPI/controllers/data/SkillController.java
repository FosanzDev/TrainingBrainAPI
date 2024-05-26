package com.fosanzdev.trainingBrainAPI.controllers.data;

import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.models.data.ProfessionalSkill;
import com.fosanzdev.trainingBrainAPI.models.data.Skill;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IProDataService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.ISkillService;
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
@RequestMapping("/pro/skills")
@Tag(name = "Skill", description = "Controlador de habilidades")
public class SkillController {

    @Autowired
    private ISkillService skillService;

    @Autowired
    private IProDataService proDataService;

    @Operation(summary = "Obtiene todas las habilidades")
    @ApiResponses( value= {
            @ApiResponse(responseCode = "200", description = "Lista de habilidades",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                    "skills": [
                                        {
                                            "id": 1,
                                            "name": "Listening",
                                            "description": "The ability to..."
                                        },
                                        {
                                            "id": 2,
                                            "name": "Speaking",
                                            "description": "The ability to..."
                                        }
                                    ]
                                    }
                                    """)))
    })
    @GetMapping("/all")
    ResponseEntity<Map<String, Object>> getSkills() {
        List<Skill> skills = skillService.getAll();
        return ResponseEntity.ok(Map.of("skills", skills));
    }

    @Operation(summary = "Obtiene las habilidades del profesional actual")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "200", description = "Lista de habilidades",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                    "professionalSkills": [
                                        {
                                            "id": 1,
                                            "name": "Listening",
                                            "description": "The ability to...",
                                            "level": 5
                                        },
                                        {
                                            "id": 2,
                                            "name": "Speaking",
                                            "description": "The ability to...",
                                            "level": 7
                                        }
                                    ]
                                    }
                                    """)))
    })
    @GetMapping("/me")
    ResponseEntity<Map<String, Object>> getMySkills(
            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer
    ) {
        try {
            String token = bearer.split(" ")[1];
            List<ProfessionalSkill> skills = proDataService.getProfessionalByAccessToken(token).getProfessionalSkills();
            return ResponseEntity.ok(Map.of("skills", skills));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Añade una habilidad al profesional actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Habilidad añadida",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                    "success": true
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Petición incorrecta",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                    "error": "Skill not found"
                                    }
                                    """)))
    })
    @PostMapping("/add")
    ResponseEntity<Map<String, Object>> addSkill(
            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Información de la habilidad a añadir", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                    "skill": 1,
                                    "level": 5
                                    }
                                    """)))
            @RequestBody Map<String, Object> body
    ) {
        try {
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null)
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

            Skill skill = skillService.getSkillById((Long) body.get("skill"));
            if (skill == null)
                return ResponseEntity.badRequest().body(Map.of("error", "Skill not found"));

            int level = (int) body.get("level");
            if (level <= 0 || level > 10)
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid skill level"));

            skillService.addNewSkill(professional, skill, level);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Elimina una habilidad del profesional actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Habilidad eliminada",
                    content = @Content(mediaType = "none")),
            @ApiResponse(responseCode = "400", description = "Petición incorrecta",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                    "error": "Skill not found"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                    "error": "Unauthorized"
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Habilidad no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                    "error": "Skill is not in your list"
                                    }
                                    """)))
    })
    @DeleteMapping("/remove/{id}")
    ResponseEntity<Map<String, Object>> deleteSkill(
            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer,
            @Parameter(description = "ID de la habilidad a eliminar", required = true)
            @PathVariable Long id
    ) {
        try {
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null)
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

            Skill skill = skillService.getSkillById(id);
            if (skill == null)
                return ResponseEntity.badRequest().body(Map.of("error", "Skill not found"));

            boolean success = skillService.deleteSkill(professional, skill);

            if (success)
                return ResponseEntity.ok().build();
            else
                return ResponseEntity.status(404).body(Map.of("error", "Skill is not in your list"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualiza una habilidad del profesional actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Habilidad actualizada",
                    content = @Content(mediaType = "none")),
            @ApiResponse(responseCode = "400", description = "Petición incorrecta",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                    "error": "Invalid skill level"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "No autorizado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                    "error": "Unauthorized"
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Habilidad no encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                    "error": "Skill is not in your list"
                                    }
                                    """)))
    })
    @PostMapping("/update")
    ResponseEntity<Map<String, Object>> updateSkill(
            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Información de la habilidad a actualizar", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                    "skill": 1,
                                    "level": 5
                                    }
                                    """)))
            @RequestBody Map<String, Object> body
    ) {
        try {
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null)
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

            Skill skill = skillService.getSkillById((Long) body.get("skill"));
            if (skill == null)
                return ResponseEntity.badRequest().body(Map.of("error", "Skill not found"));

            int level = (int) body.get("level");
            if (level <= 0 || level > 10)
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid skill level"));

            boolean success = skillService.updateSkill(professional, skill, level);
            if (success)
                return ResponseEntity.ok().build();
            else
                return ResponseEntity.status(404).body(Map.of("error", "Skill is not in your list"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
