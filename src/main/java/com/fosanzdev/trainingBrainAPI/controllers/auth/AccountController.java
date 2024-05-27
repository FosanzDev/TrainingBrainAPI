package com.fosanzdev.trainingBrainAPI.controllers.auth;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.services.interfaces.auth.IAccountService;
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
                            schema = @Schema(example = "{\"username\":\"usuario\",\"isVerified\":\"true/false\",\"id\":\"364f2933-c91e-4641-...\",\"name\":\"Minombre\",  \"isProfessional\":\"true/false\", \"email\":\"mi@correo.com\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Unauthorized\"}")))
    })
    @GetMapping("/me")
    ResponseEntity<Map<String, Object>> me(
            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer) {
        try{
            String token = bearer.split(" ")[1];
            Account account = accountService.getAccountByAccessToken(token);
            if (account != null) {
                return ResponseEntity.ok(account.toMap());
            } else {
                Map<String, Object> error = Map.of("error", "Unauthorized");
                return ResponseEntity.status(401).body(error);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtiene la información de una cuenta por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información COMPLETA de la cuenta (Solo para cuentas profesionales)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"username\":\"usuario\",\"isVerified\":\"true/false\",\"id\":\"364f2933-c91e-4641-...\",\"name\":\"Minombre\",  \"isProfessional\":\"true/false\", \"email\":\"mi@correo.com\"}"))),
            @ApiResponse(responseCode = "206", description = "Información BÁSICA de la cuenta (Solo para cuentas no profesionales)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"username\":\"usuario\",\"id\":\"364f2933-c91e-4641-...\", \"name\":\"Minombre\"}"))),
    })
    @GetMapping("/{id}")
    ResponseEntity<Map<String, Object>> getById(
            @Parameter(description = "Token de autorización", required = true, example="Bearer <token>")
            @RequestHeader("Authorization") String bearer,

            @Parameter(description = "ID de la cuenta", required = true, example = "364f2933-c91e-4641-...")
            @PathVariable String id) {

        try{
            String token = bearer.split(" ")[1];
            Account account = accountService.getAccountByAccessToken(token);
            if (account.getId().equals(id) || account.isProfessional()) {
                Account accountById = accountService.getAccountById(id);
                if (accountById != null) {
                    return ResponseEntity.ok(accountById.toMap());
                } else {
                    return ResponseEntity.badRequest().build();
                }
            } else {
                Account accountById = accountService.getAccountById(id);
                if (accountById != null) {
                    return ResponseEntity.ok(accountById.toBasicMap());
                } else {
                    return ResponseEntity.badRequest().build();
                }
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualiza la información de la cuenta actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información actualizada",
                    content = @Content(mediaType = "none")),
            @ApiResponse(responseCode = "400", description = "Account malformed",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Account malformed\"}"))),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Account not found\"}")))
    })
    @PostMapping("/update")
    ResponseEntity<Map<String, Object>> updateAccount(
            @Parameter(description = "Token de autorización", required = true, example="Bearer <token>")
            @RequestHeader("Authorization") String bearer,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Información actualizada de la cuenta", required = true,
            content= @Content(mediaType = "application/json",
                    schema = @Schema(example = "{\"username\":\"usuario\",\"name\":\"Minombre\", \"email\":\"mi@correo.com\", \"password\":\"micontraseña\"}")))
            @RequestBody Account account) {
        try{
            String token = bearer.split(" ")[1];

            Account accountToUpdate = accountService.getAccountByAccessToken(token);
            if (accountToUpdate == null)
                return ResponseEntity.status(404).body(Map.of("error", "Account not found"));

            try{
                accountService.updateAccount(accountToUpdate, account);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Account malformed"));
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Account malformed"));
        }
    }
}
