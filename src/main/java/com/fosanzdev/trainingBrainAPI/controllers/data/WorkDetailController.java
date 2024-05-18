package com.fosanzdev.trainingBrainAPI.controllers.data;

import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.models.details.WorkDetail;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IProDataService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IWorkDetailService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/pro/workdetails")
@RestController
public class WorkDetailController {

    @Autowired
    private IWorkDetailService workDetailService;

    @Autowired
    private IProDataService proDataService;

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

    @PostMapping("/add")
    ResponseEntity<Map<String, Object>> addWorkDetail(
            @Parameter(description = "Token de autenticación", required = true, example = "Bearer <token")
            @RequestHeader("Authorization") String bearer,
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

    @DeleteMapping("/remove/{id}")
    ResponseEntity<Map<String, Object>> removeWorkDetail(
            @Parameter(description = "Token de autenticación", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer,
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

    @PostMapping("/edit/{id}")
    ResponseEntity<Map<String, Object>> editWorkDetail(
            @Parameter(description = "Token de autenticación", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer,
            @PathVariable Long id,
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
