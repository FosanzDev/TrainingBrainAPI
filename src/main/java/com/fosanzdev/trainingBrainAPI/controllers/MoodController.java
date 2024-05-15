package com.fosanzdev.trainingBrainAPI.controllers;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.models.mood.AccountMood;
import com.fosanzdev.trainingBrainAPI.services.accounts.AccountService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IAccountService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IMoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    ){
        String token = bearer.split(" ")[1]; // Bearer token
        String moodId = body.get("moodId");
        Account account = accountService.getAccountByAccessToken(token);

        //TEST: AddEntry
        moodService.addEntry(account.getId(), moodId);

        //TEST: List of entries
        List<AccountMood> accountMoods = moodService.getHistory(account.getId());
        for (AccountMood accountMood : accountMoods) {
            System.out.println(accountMood.getMood().getName());
        }

        //Test: List entries with limit and offset
        List<AccountMood> accountMoods2 = moodService.getHistory(account.getId(), 2, 0);
        for (AccountMood accountMood : accountMoods2) {
            System.out.println(accountMood.getMood().getName());
        }

        return ResponseEntity.ok(Map.of("message", "Entry added"));
    }
}
