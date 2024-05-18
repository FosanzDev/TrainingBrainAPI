package com.fosanzdev.trainingBrainAPI.controllers.data;

import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.models.details.ProfessionalSkill;
import com.fosanzdev.trainingBrainAPI.models.details.Skill;
import com.fosanzdev.trainingBrainAPI.repositories.data.SkillRepository;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IProDataService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.ISkillService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pro/skills")
public class SkillController {

    @Autowired
    private ISkillService skillService;

    @Autowired
    private IProDataService proDataService;

    @GetMapping("/all")
    ResponseEntity<Map<String, Object>> getSkills(){
        List<Skill> skills = skillService.getAll();
        return ResponseEntity.ok(Map.of("skills", skills));
    }

    @GetMapping("/me")
    ResponseEntity<Map<String, Object>> getMySkills(
            @Parameter(description = "Token de autorización", required = true, example="Bearer <token>")
            @RequestHeader("Authorization") String bearer
    ){
        try {
            String token = bearer.split(" ")[1];
            List<ProfessionalSkill> skills = proDataService.getProfessionalByAccessToken(token).getProfessionalSkills();
            return ResponseEntity.ok(Map.of("skills", skills));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/add")
    ResponseEntity<Map<String, Object>> addSkill(
            @Parameter(description = "Token de autorización", required = true, example="Bearer <token>")
            @RequestHeader("Authorization") String bearer,
            @RequestBody Map<String, Object> body
    ){
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

            boolean success = skillService.addNewSkill(professional, skill, level);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/remove/{id}")
    ResponseEntity<Map<String, Object>> deleteSkill(
            @Parameter(description = "Token de autorización", required = true, example="Bearer <token>")
            @RequestHeader("Authorization") String bearer,
            @PathVariable Long id
    ){
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

    @PostMapping("/update")
    ResponseEntity<Map<String, Object>> updateSkill(
            @Parameter(description = "Token de autorización", required = true, example="Bearer <token>")
            @RequestHeader("Authorization") String bearer,
            @RequestBody Map<String, Object> body
    ){
        try{
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
