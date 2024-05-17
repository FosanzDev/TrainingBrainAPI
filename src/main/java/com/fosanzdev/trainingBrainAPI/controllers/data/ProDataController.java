package com.fosanzdev.trainingBrainAPI.controllers.data;

import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IAccountService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IProDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/pro")
@RestController
@Tag(name = "Professional Data", description = "Controlador de datos de usuario profesional")
public class ProDataController {

    @Autowired
    private IProDataService proDataService;

    @Operation(summary = "Obtiene la información del usuario profesional actual")
    @GetMapping("/me")
    ResponseEntity<Map<String, Object>> me(
            @RequestHeader("Authorization") String bearer
    ){
        try{
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional != null) {
                return ResponseEntity.ok(professional.toMap());
            } else {
                return ResponseEntity.status(401).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtiene la información de un usuario profesional por su ID")
    @GetMapping("/{id}")
    ResponseEntity<Map<String, Object>> getProfessional(
            @PathVariable String id
    ) {
        try{
            Professional professional = proDataService.getProfessionalByAccountId(id);
            if (professional != null) {
                return ResponseEntity.ok(professional.toMap());
            } else {
                return ResponseEntity.status(401).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualiza la información del usuario profesional actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información actualizada correctamente"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Unauthorized\"}")))
    })
    @PostMapping("/update")
    ResponseEntity<Map<String, Object>> updateProfessional(
            @RequestHeader("Authorization") String bearer,
            @RequestBody Professional updatedProfessional
    ) {
        try {
            String token = bearer.split(" ")[1];
            Professional proToUpdate = proDataService.getProfessionalByAccessToken(token);
            if (proToUpdate != null) {
                proDataService.updateProfessional(proToUpdate, updatedProfessional);
            } else {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
}
