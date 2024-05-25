package com.fosanzdev.trainingBrainAPI.controllers.goals;

import com.fosanzdev.trainingBrainAPI.models.details.User;
import com.fosanzdev.trainingBrainAPI.models.goals.Goal;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IUserDataService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.goals.IGoalService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/goals")
@RestController
@Tag(name = "Goals", description = "Controlador de metas")
public class GoalController {

    @Autowired
    private IGoalService goalService;

    @Autowired
    private IUserDataService userDataService;

    @PostMapping("/add")
    ResponseEntity<Map<String, Object>> addGoal(
            @RequestHeader("Authorization") String bearer,
            @RequestBody Map<String, Object> body
    ) {
        try {
            String token = bearer.split(" ")[1]; // Bearer token
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

            Goal goal = Goal.fromMap(body);
            if (goal == null) return ResponseEntity.badRequest().body(Map.of("message", "Invalid goal"));

            if (goalService.addGoal(goal, user)) {
                return ResponseEntity.ok(Map.of("message", "Goal added"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid goal"));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/done/{id}")
    ResponseEntity<Map<String, Object>> markGoalAsDone(
            @RequestHeader("Authorization") String bearer,
            @PathVariable String id
    ) {
        try {
            String token = bearer.split(" ")[1]; // Bearer token
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

            Goal goal = goalService.getGoal(user, id);
            if (goal == null) return ResponseEntity.badRequest().body(Map.of("message", "No goal found"));

            boolean result = goalService.markGoalAsDone(goal);
            if (!result) return ResponseEntity.badRequest().body(Map.of("message", "Goal already completed or done today"));

            return ResponseEntity.ok().body(Map.of("message", "Goal marked as done"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/today")
    ResponseEntity<Map<String, Object>> getTodayGoals(
            @RequestHeader("Authorization") String bearer
    ) {
        try{
            String token = bearer.split(" ")[1]; // Bearer token
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

            List<Goal> goals = goalService.getAllGoals(user);
            if (goals == null) return ResponseEntity.badRequest().body(Map.of("message", "No goals found"));

            List<Map<String, Object>> goalsMap = new ArrayList<>();
            for (Goal goal : goals) {
                Map<String, Object> goalMap = new HashMap<>(goal.toMap());
                if (goalService.pendingToday(goal.getId())) {
                    goalMap.put("pendingToday", true);
                    goalsMap.add(goalMap);
                }
            }


            return ResponseEntity.ok().body(Map.of("goals", goalsMap));
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/get/{id}")
    ResponseEntity<Map<String, Object>> getGoal(
            @RequestHeader("Authorization") String bearer,
            @PathVariable String id
    ) {
        try{
            String token = bearer.split(" ")[1]; // Bearer token
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

            Goal goal = goalService.getGoal(user, id);
            if (goal == null) return ResponseEntity.badRequest().body(Map.of("message", "No goal found"));

            Map<String, Object> goalMap = new HashMap<>(goal.toMap());
            goalMap.put("pendingToday", goalService.pendingToday(goal.getId()));

            return ResponseEntity.ok().body(goalMap);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/all")
    ResponseEntity<Map<String, Object>> getAllGoals(
            @RequestHeader("Authorization") String bearer
    ) {
        try{
            String token = bearer.split(" ")[1]; // Bearer token
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

            List<Goal> goals = goalService.getAllGoals(user);
            if (goals == null) return ResponseEntity.badRequest().body(Map.of("message", "No goals found"));


            List<Map<String, Object>> goalsMap = new ArrayList<>();
            for (Goal goal : goals) {
                Map<String, Object> goalMap = new HashMap<>(goal.toMap());
                goalMap.put("pendingToday", goalService.pendingToday(goal.getId()));
                goalsMap.add(goalMap);
            }

            return ResponseEntity.ok().body(Map.of("goals", goalsMap));
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<Map<String, Object>> deleteGoal(
            @RequestHeader("Authorization") String bearer,
            @PathVariable String id
    ) {
        try{
            String token = bearer.split(" ")[1]; // Bearer token
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));

            boolean result = goalService.deleteGoal(user, id);
            if (!result) return ResponseEntity.badRequest().body(Map.of("message", "No goal found"));

            return ResponseEntity.ok().body(Map.of("message", "Goal deleted"));
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

}
