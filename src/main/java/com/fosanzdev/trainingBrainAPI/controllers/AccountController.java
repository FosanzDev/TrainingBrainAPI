package com.fosanzdev.trainingBrainAPI.controllers;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IAccountService;
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

import java.util.Map;

@RequestMapping("/account")
@RestController
@Tag(name = "Account", description = "Controlador de cuentas")
public class AccountController {

    @Autowired
    private IAccountService accountService;

    @Operation(summary = "Obtiene la información de la cuenta actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información de la cuenta",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"username\":\"usuario\",\"isVerified\":\"true/false\",\"id\":\"364f2933-c91e-4641-...\",\"name\":\"Minombre\",  \"isProfessional\":\"true/false\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Unauthorized\"}")))
    })
    @GetMapping("/me")
    ResponseEntity<Map<String, String>> me(
            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer) {
        try{
            String token = bearer.split(" ")[1];
            Account account = accountService.getAccountByAccessToken(token);
            if (account != null) {
                return ResponseEntity.ok(account.toMap());
            } else {
                Map<String, String> error = Map.of("error", "Unauthorized");
                return ResponseEntity.status(401).body(error);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtiene la información de una cuenta por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información COMPLETA de la cuenta (Solo para cuentas profesionales)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"username\":\"usuario\",\"isVerified\":\"true/false\",\"id\":\"364f2933-c91e-4641-...\",\"name\":\"Minombre\",  \"isProfessional\":\"true/false\"}"))),
            @ApiResponse(responseCode = "206", description = "Información BÁSICA de la cuenta (Solo para cuentas no profesionales)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"username\":\"usuario\",\"id\":\"364f2933-c91e-4641-...\"}"))),
    })
    @GetMapping("/id/{id}")
    ResponseEntity<Map<String, String>> getById(
            @Parameter(description = "Token de autorización", required = true, example="Bearer <token>")
            @RequestHeader("Authorization") String bearer,

            @Parameter(description = "ID de la cuenta", required = true, example = "364f2933-c91e-4641-...")
            @PathVariable String id) {

        try{
            String token = bearer.split(" ")[1];
            Account account = accountService.getAccountByAccessToken(token);
            if (account.getId().equals(id) || account.isProfessional()) {
                return ResponseEntity.ok(account.toMap());
            } else {
                return ResponseEntity.status(206).body(account.toBasicMap());
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
