package com.fosanzdev.trainingBrainAPI.controllers.data;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.models.details.User;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IAccountService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IUserDataService;
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

@RequestMapping("/user")
@RestController
@Tag(name = "User Data", description = "Controlador de datos de usuario")
public class UserDataController {

    @Autowired
    private IUserDataService userDataService;

    @Autowired
    private IAccountService accountService;

    @Operation(summary = "Obtiene la información del usuario actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información del usuario",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"username\":\"usuario\",\"isVerified\":\"true/false\",\"id\":\"364f2933-c91e-4641-...\",\"name\":\"Minombre\",  \"isProfessional\":\"true/false\"}"))),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado o no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Unauthorized\"}")))
    })
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

    @Operation(summary = "Obtiene la información de un usuario por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información COMPLETA del usuario (Solo para cuentas profesionales o propias)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"id\":\"364f2933-c91e-4641-...\",\"publicBio\":\"Bio pública\",\"privateBio\":\"Bio privada\",\"history\":\"Historial\",\"dateOfBirth\":\"2000-01-01\"}"))),
            @ApiResponse(responseCode = "206", description = "Información PÚBLICA del usuario (Solo para cuentas normales)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"username\":\"usuario\",\"id\":\"364f2933-c91e-4641-...\"}"))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"User not found\"}")))
    })
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
                return ResponseEntity.status(404).body(Map.of("error", "User not found or not verified"));

            if (account.getId().equals(user.getAccount().getId()) || account.isProfessional())
                return ResponseEntity.ok(user.toMap());

            else
                return ResponseEntity.status(206).body(user.toPublicMap());
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

            User userToUpdate = userDataService.getUserByAccessToken(token);
            if (userToUpdate == null)
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));


            if (user.getPublicBio() != null)
                userToUpdate.setPublicBio(user.getPublicBio());
            if (user.getPrivateBio() != null)
                userToUpdate.setPrivateBio(user.getPrivateBio());
            if (user.getHistory() != null)
                userToUpdate.setHistory(user.getHistory());
            if (user.getDateOfBirth() != null)
                try{
                    userToUpdate.setDateOfBirth(user.getDateOfBirth());
                } catch (Exception e) {
                    return ResponseEntity.status(400).body(Map.of("error", "Invalid date format"));
                }

            userDataService.updateUser(userToUpdate);
            return ResponseEntity.ok(userToUpdate.toMap());

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
