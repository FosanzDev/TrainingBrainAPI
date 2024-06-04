package com.fosanzdev.trainingBrainAPI.controllers.data;

import com.fosanzdev.trainingBrainAPI.models.data.User;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IUserDataService;
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

    @Operation(summary = "Obtiene la información del usuario actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información del usuario",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "id":"364f2933-c91e-4641-...",
                                        "publicBio":"Bio pública",
                                        "privateBio":"Bio privada",
                                        "history":"Historial",
                                        "dateOfBirth":"01/01/2000"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado o no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Unauthorized\"}")))
    })
    @GetMapping("/me")
    ResponseEntity<Map<String, String>> me(
            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer
    ) {
        try {
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user != null) {
                return ResponseEntity.ok(user.toMap());
            } else {
                Map<String, String> error = Map.of("error", "Unauthorized");
                return ResponseEntity.status(401).body(error);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtiene la información de un usuario por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información COMPLETA del usuario (Solo para cuentas profesionales o propias)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "id":"364f2933-c91e-4641-...",
                                        "name":"Nombre",
                                        "publicBio":"Bio pública",
                                        "privateBio":"Bio privada",
                                        "history":"Historial",
                                        "dateOfBirth":"01/01/2000"
                                    }
                                    """))),
            @ApiResponse(responseCode = "206", description = "Información PÚBLICA del usuario (Solo para cuentas normales)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "id":"364f2933-c91e-4641-...",
                                        "name":"Nombre",
                                        "publicBio":"Bio pública",
                                        "dateOfBirth":"01/01/2000"
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"User not found\"}")))
    })
    @GetMapping("/{id}")
    ResponseEntity<Map<String, String>> getUser(
            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer,
            @Parameter(description = "ID del usuario", required = true, example = "364f2933-c91e-4641-...")
            @PathVariable String id
    ) {
        try {
            String token = bearer.split(" ")[1];
            User currentUser = userDataService.getUserByAccessToken(token);
            if (currentUser == null)
                return ResponseEntity.status(404).body(Map.of("error", "Account not found"));

            User user = userDataService.getUserByAccountID(id);
            if (user == null)
                return ResponseEntity.status(404).body(Map.of("error", "User not found or not verified"));

            if (currentUser.getId().equals(user.getId()) || currentUser.getAccount().isProfessional())
                return ResponseEntity.ok(user.toMap());

            else
                return ResponseEntity.status(206).body(user.toPublicMap());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualiza la información del usuario actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información actualizada",
                    content = @Content(mediaType = "none")),
            @ApiResponse(responseCode = "400", description = "Formato de fecha inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Invalid date format\"}"))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"User not found\"}")))
    })
    @PostMapping("/update")
    ResponseEntity<Map<String, String>> updateUser(
            @Parameter(description = "Token de autorización", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String bearer,
            @Parameter(description = "Información del usuario a actualizar", required = true,
            content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                                    {
                                        "publicBio":"Bio pública",
                                        "privateBio":"Bio privada",
                                        "history":"Historial",
                                        "dateOfBirth":"01/01/2000"
                                    }
                                    """)))
            @RequestBody User user) {
        try {
            String token = bearer.split(" ")[1];

            User userToUpdate = userDataService.getUserByAccessToken(token);
            if (userToUpdate == null)
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));

            try {
                userDataService.updateUser(userToUpdate, user);
            } catch (Exception e) {
                return ResponseEntity.status(400).body(Map.of("error", "Invalid date format"));
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
