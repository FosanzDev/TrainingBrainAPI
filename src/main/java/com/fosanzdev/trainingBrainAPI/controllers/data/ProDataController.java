package com.fosanzdev.trainingBrainAPI.controllers.data;

import com.fosanzdev.trainingBrainAPI.models.details.Branch;
import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.models.details.WorkTitle;
import com.fosanzdev.trainingBrainAPI.repositories.data.BranchRepository;
import com.fosanzdev.trainingBrainAPI.repositories.data.WorkTitleRepository;
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

@RequestMapping("/pro")
@RestController
@Tag(name = "Professional Data", description = "Controlador de datos de usuario profesional")
public class ProDataController {

    @Autowired
    private IProDataService proDataService;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private WorkTitleRepository workTitleRepository;

    @Operation(summary = "Obtiene la información del usuario profesional actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información obtenida correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(anyOf = Professional.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Unauthorized\"}")))
    })
    @GetMapping("/me")
    ResponseEntity<Map<String, Object>> me(
            @RequestHeader("Authorization") String bearer
    ) {
        try {
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información obtenida correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(anyOf = Professional.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Unauthorized\"}")))
    })
    @GetMapping("/{id}")
    ResponseEntity<Map<String, Object>> getProfessional(
            @PathVariable String id
    ) {
        try {
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
            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Información actualizada del usuario profesional", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(exampleClasses = Professional.class)))
            @RequestBody Professional updatedProfessional
    ) {
        try {
            String token = bearer.split(" ")[1];

            Professional proToUpdate = proDataService.getProfessionalByAccessToken(token);
            if (proToUpdate == null)
                return ResponseEntity.status(404).body(Map.of("error", "Professional not found"));

            try{
                proDataService.updateProfessional(proToUpdate, updatedProfessional);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/setWorkTitle")
    ResponseEntity<Map<String, Object>> setWorkTitle(
            @Parameter(description = "Token de autorización", required = true, example="Bearer <token>")
            @RequestHeader("Authorization") String bearer,
            @RequestBody Long workTitleId
    ){
        try {
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null)
                return ResponseEntity.status(404).body(Map.of("error", "Professional not found"));

            if (proDataService.setWorkTitle(professional, workTitleId))
                return ResponseEntity.ok().build();
            else
                return ResponseEntity.status(404).body(Map.of("error", "Work title not found"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/branches")
    ResponseEntity<Map<String, Object>> getBranchList(){
        List<Branch> branches = branchRepository.findAll();
        return ResponseEntity.ok(Map.of("branches", branches));
    }

    @GetMapping("/worktitles")
    ResponseEntity<Map<String, Object>> getWorkTitleList(){
        List<WorkTitle> workTitles = workTitleRepository.findAll();
        return ResponseEntity.ok(Map.of("workTitles", workTitles));
    }

    @GetMapping("/worktitles/bybranch/{branchId}")
    ResponseEntity<Map<String, Object>> getWorkTitleListByBranch(
            @PathVariable Long branchId
    ){
        List<WorkTitle> workTitles = workTitleRepository.findByBranch(branchId);
        return ResponseEntity.ok(Map.of("workTitles", workTitles));
    }
}