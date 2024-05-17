package com.fosanzdev.trainingBrainAPI.controllers.auth;

import com.fosanzdev.trainingBrainAPI.models.auth.AccessToken;
import com.fosanzdev.trainingBrainAPI.models.auth.AuthCode;
import com.fosanzdev.trainingBrainAPI.models.auth.RefreshToken;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RequestMapping("/auth")
@RestController
@Tag(name="Auth", description = "Controlador de autenticación")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @Operation(summary = "Genera el proceso de inicio de sesión")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Codigo de autorización generado correctamente",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                            {"authToken": "2ksh31ls-wsiduoia-..."}
                            """))),
            @ApiResponse(responseCode = "400", description = "Datos de inicio de sesión inválidos",
            content = @Content(mediaType = "none"))
    }

    )
    @PostMapping("/login")
    ResponseEntity<Map<String, String>> login(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de inicio de sesión",
            required = true,
            content = @Content(
                    schema = @Schema(
                            example = """
                            {"username": "user", "password": "pass"}
                            """
                    )
            )
    )  @RequestBody Map<String, String> body) {
        //Get username and password from body
        String username = body.get("username");
        String password = body.get("password");

        boolean validAccount = authService.validAccount(username, password, false);

        if (validAccount) {
            authService.forceLogout(username);
            AuthCode code = authService.createAuthCode(username);

            Map<String, String> response = new HashMap<>();
            response.put("authToken", code.getCode());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Verifica y completa el proceso de registro e inicio de sesión")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión completado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                            {"refreshToken": "2ksh31ls-wsiduoia-...", "accessToken": "2ksh31ls-wsiduoia-..."}
                            """))),
            @ApiResponse(responseCode = "400", description = "Datos de inicio de sesión inválidos",
                    content = @Content(mediaType = "none"))
    })
    @PostMapping("/verify")
    ResponseEntity<Map<String, String>> verify(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de inicio de sesión",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    example = """
                            {"username": "user", "password": "pass", "authToken": "2ksh31ls-wsiduoia-..."}
                            """
                            )
                    )
            )
            @RequestBody Map<String, String> body) {
        //Get username, password and authToken from body
        String username = body.get("username");
        String password = body.get("password");
        String authToken = body.get("authToken");

        //Verify the auth code
        boolean validAccount = authService.validAccount(username, password, true);
        boolean validAuthCode = authService.validateAuthCode(authToken, username);
        if (validAccount && validAuthCode) {
            authService.invalidateAuthCode(authToken);
            RefreshToken refreshToken = authService.createRefreshToken(username);
            AccessToken accessToken = authService.createAccessToken(username);

            Map<String, String> response = new HashMap<>();
            response.put("refreshToken", refreshToken.getToken());
            response.put("accessToken", accessToken.getToken());

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Refresca el token de acceso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token de acceso refrescado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                            {"accessToken": "2ksh31ls-wsiduoia-..."}
                            """))),
            @ApiResponse(responseCode = "400", description = "Datos de inicio de sesión inválidos",
                    content = @Content(mediaType = "none"))
    })
    @PostMapping("/refresh")
    ResponseEntity<Map<String, String>> refresh(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de inicio de sesión",
            required = true,
            content = @Content(
                    schema = @Schema(
                            example = """
                            {"refreshToken": "2ksh31ls-wsiduoia-...", "accessToken": "2ksh31ls-wsiduoia-..."}
                            """
                    )
            )
    )
            @RequestBody Map<String, String> body) {
        // Get the request parameters
        String refreshToken = body.get("refreshToken");
        String accessToken = body.get("accessToken");

        // Validate the sent tokens
        boolean valid = authService.validateRefreshToken(refreshToken, accessToken);

        if (valid) {
            // Refresh the access token and return it
            AccessToken newAccessToken = authService.refreshAccessToken(refreshToken, accessToken);

            Map<String, String> response = new HashMap<>();
            response.put("accessToken", newAccessToken.getToken());

            return ResponseEntity.ok(response);

        } else {
            return ResponseEntity.badRequest().build();
        }
    }


    @Operation(summary = "Registra un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = """
                            {"authToken": "2ksh31ls-wsiduoia-..."}
                            """))),
            @ApiResponse(responseCode = "409", description = "Usuario ya registrado",
                    content = @Content(mediaType = "none"))
    })
    @PostMapping("/register")
    ResponseEntity<Map<String, String>> register(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de inicio de sesión",
            required = true,
            content = @Content(
                    schema = @Schema(
                            example = """
                            {"username": "user", "password": "pass", "name": "name", "professional": "true/false"}
                            """
                    )
            )
    )
            @RequestBody Map<String, String> body) {
        //Get username, name and password from body
        String username = body.get("username");
        String name = body.get("name");
        String password = body.get("password");
        if (body.get("professional") == null) {
            return ResponseEntity.badRequest().build();
        }
        boolean professional = Boolean.parseBoolean(body.get("professional"));

        AuthCode authCode = authService.register(name, username, password, professional);

        if (authCode != null) {
            Map<String, String> response = new HashMap<>();
            response.put("authToken", authCode.getCode());
            return ResponseEntity.ok(response);
        } else {
            // Return 409 (conflict) if the account already exists
            return ResponseEntity.status(409).build();
        }
    }

    @Operation(summary = "Cierra la sesión de un usuario e invalida sus tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sesión cerrada correctamente",
                    content = @Content(mediaType = "none")),
            @ApiResponse(responseCode = "400", description = "Datos de inicio de sesión inválidos",
                    content = @Content(mediaType = "none"))
    })
    @PostMapping("/logout")
    ResponseEntity<Map<String, String>> logout(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de inicio de sesión",
            required = true,
            content = @Content(
                    schema = @Schema(
                            example = """
                            {"username": "user", "refreshToken": "2ksh31ls-wsiduoia-..."}
                            """
                    )
            )
    )

            @RequestBody Map<String, String> body) {
        // Get the request parameters
        String username = body.get("username");
        String refreshToken = body.get("refreshToken");

        boolean success = authService.logout(username, refreshToken);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
