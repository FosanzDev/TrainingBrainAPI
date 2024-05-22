package com.fosanzdev.trainingBrainAPI.controllers.appointments;

import com.fosanzdev.trainingBrainAPI.models.appointments.ProfessionalSchedule;
import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.services.interfaces.appointments.IProScheduleService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IProDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pro/schedule")
public class ScheduleController {

    @Autowired
    private IProScheduleService proScheduleService;

    @Autowired
    private IProDataService proDataService;

    @PostMapping("/modify")
    ResponseEntity<Map<String, Object>> modifySchedule(
            @RequestHeader("Authorization") String bearer,
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

    @GetMapping("/get/{professionalId}")
    ResponseEntity<Map<String, Object>> getSchedule(
            @PathVariable String professionalId
    ) {
        List<ProfessionalSchedule> schedules = proScheduleService.findByProfessionalId(professionalId);
        if (schedules == null || schedules.isEmpty()) return ResponseEntity.badRequest().body(Map.of("message", "No schedule found"));
        else return ResponseEntity.ok(ProfessionalSchedule.toNamedDayJsonSchedule(schedules));
    }
}