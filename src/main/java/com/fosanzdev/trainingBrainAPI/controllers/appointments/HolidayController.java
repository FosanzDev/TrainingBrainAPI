package com.fosanzdev.trainingBrainAPI.controllers.appointments;

import com.fosanzdev.trainingBrainAPI.models.appointments.ProfessionalHoliday;
import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.services.interfaces.appointments.IProHolidaysService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.appointments.IProScheduleService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IProDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public void removeHoliday() {
        //TODO
    }

    @GetMapping("/list/me")
    public void listMyHolidays() {
        //TODO
    }

    @GetMapping("/list/{professionalId}")
    public void listHolidays() {
        //TODO
    }

}
