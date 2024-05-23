package com.fosanzdev.trainingBrainAPI.controllers.appointments;

import com.fosanzdev.trainingBrainAPI.models.appointments.ProfessionalHoliday;
import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.services.interfaces.appointments.IProHolidaysService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.appointments.IProScheduleService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IProDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pro/holiday")
public class HolidayController {

    @Autowired
    private IProHolidaysService proHolidaysService;

    @Autowired
    private IProScheduleService proScheduleService;

    @Autowired
    private IProDataService proDataService;

    @PostMapping("/add")
    ResponseEntity<Map<String, Object>> addHoliday(
            @RequestHeader("Authorization") String bearer,
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
            return ResponseEntity.status(400).body(Map.of("message", "Bad request"));
        }

        return ResponseEntity.status(400).body(Map.of("message", "Bad request. Check dates are not overlapping."));
    }

    @DeleteMapping("/remove/{holidayId}")
    ResponseEntity<Map<String, Object>> removeHoliday(
            @PathVariable String holidayId,
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

    @GetMapping("/list/me")
    ResponseEntity<Map<String, Object>> listMyHolidays(
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

    @GetMapping("/list/{professionalId}")
    ResponseEntity<Map<String, Object>> listHolidays(
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
