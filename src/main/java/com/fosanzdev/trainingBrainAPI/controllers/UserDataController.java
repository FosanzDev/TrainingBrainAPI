package com.fosanzdev.trainingBrainAPI.controllers;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.models.details.User;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IAccountService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IUserDataService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/user")
@RestController
@Tag(name = "User", description = "Controlador de datos de usuario")
public class UserDataController {

    @Autowired
    private IUserDataService userDataService;

    @Autowired
    private IAccountService accountService;

    @GetMapping("/me")
    ResponseEntity<Map<String, String>> me(
            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer
    ) {
        try{
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user != null) {
                return ResponseEntity.ok(user.toMap());
            } else {
                Map<String, String> error = Map.of("error", "Unauthorized");
                return ResponseEntity.status(401).body(error);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    ResponseEntity<Map<String, String>> getUser(
            @Parameter(description = "ID de la cuenta", required = true)
            @RequestHeader("Authorization") String bearer,
            @PathVariable String id
    ) {
        try{
            String token = bearer.split(" ")[1];
            Account account = accountService.getAccountByAccessToken(token);
            if (account == null)
                return ResponseEntity.status(404).body(Map.of("error", "Account not found"));

            User user = userDataService.getUserByAccountID(id);
            if (user == null)
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));

            if (account.getId().equals(user.getAccount().getId()) || account.isProfessional())
                return ResponseEntity.ok(user.toMap());

            else
                return ResponseEntity.ok(user.toPublicMap());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/update")
    ResponseEntity<Map<String, String>> updateUser(
            @Parameter(description = "Token de autorización", required = true, example="Bearer <token>")
            @RequestHeader("Authorization") String bearer,
            @RequestBody User user) {
        try{
            String token = bearer.split(" ")[1];
            Account account = accountService.getAccountByAccessToken(token);
            if (account == null)
                return ResponseEntity.status(404).body(Map.of("error", "Account not found"));

            User userToUpdate = userDataService.getUserByAccountID(account.getId());
            if (userToUpdate == null)
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));


            if (user.getPublicBio() != null)
                userToUpdate.setPublicBio(user.getPublicBio());
            if (user.getPrivateBio() != null)
                userToUpdate.setPrivateBio(user.getPrivateBio());
            if (user.getHistory() != null)
                userToUpdate.setHistory(user.getHistory());
            if (user.getDateOfBirth() != null)
                userToUpdate.setDateOfBirth(user.getDateOfBirth());

            userDataService.updateUser(userToUpdate);
            return ResponseEntity.ok(userToUpdate.toMap());

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
