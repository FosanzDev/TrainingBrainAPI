package com.fosanzdev.trainingBrainAPI.controllers;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.models.mood.AccountMood;
import com.fosanzdev.trainingBrainAPI.models.mood.Mood;
import com.fosanzdev.trainingBrainAPI.services.accounts.AccountService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IAccountService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IMoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mood")
public class MoodController {

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IMoodService moodService;

    @PostMapping("/add")
    ResponseEntity<Map<String, String>> addEntry(
            @RequestHeader("Authorization") String bearer,
            @RequestBody Map<String, String> body
    ) {
        String token = bearer.split(" ")[1]; // Bearer token
        String moodId = body.get("moodId");
        Account account = accountService.getAccountByAccessToken(token);

        if (account == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid token"));
        }

        try{
            moodService.addEntry(account.getId(), moodId);
            return ResponseEntity.ok(Map.of("message", "Entry added"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid request, check moodId"));
        }
    }

    @GetMapping("/history")
    ResponseEntity<Map<String, Object>> getHistory(
            @RequestHeader("Authorization") String bearer,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset
    ) {
        String token = bearer.split(" ")[1]; // Bearer token
        Account account = accountService.getAccountByAccessToken(token);

        if (account == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid token"));
        }

        List<AccountMood> accountMoods;
        accountMoods = moodService.getHistory(account.getId(), limit, offset);

        return ResponseEntity.ok(Map.of("history", accountMoods));
    }

    @GetMapping("/all")
    ResponseEntity<Map<String, List<Mood>>> getMoods() {
        List<Mood> accountMoods = moodService.getMoods();
        return ResponseEntity.ok(Map.of("moods", accountMoods));
    }

}
