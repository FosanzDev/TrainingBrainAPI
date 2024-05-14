package com.fosanzdev.trainingBrainAPI.controllers;

import com.fosanzdev.trainingBrainAPI.models.AccessToken;
import com.fosanzdev.trainingBrainAPI.models.AuthCode;
import com.fosanzdev.trainingBrainAPI.models.RefreshToken;
import com.fosanzdev.trainingBrainAPI.services.auth.interfaces.IAuthService;
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
@Tag(name="Controlador de autenticación")
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
            @ApiResponse(responseCode = "400", description = "Datos de inicio de sesión inválidos")
    }

    )
    @PostMapping("/login")
    ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
        //Get username and password from body
        String username = body.get("username");
        String password = body.get("password");

        boolean validAccount = authService.verifyAccount(username, password, false);

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

    @PostMapping("/verify")
    ResponseEntity<Map<String, String>> verify(@RequestBody Map<String, String> body) {
        //Get username, password and authToken from body
        String username = body.get("username");
        String password = body.get("password");
        String authToken = body.get("authToken");

        //Verify the auth code
        boolean validAccount = authService.verifyAccount(username, password, true);
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

    @PostMapping("/refresh")
    ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> body) {
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

    @PostMapping("/register")
    void register(@RequestBody Map<String, String> body) {
        //Get username, name and password from body
        String username = body.get("username");
        String name = body.get("name");
        String password = body.get("password");

        AuthCode authCode = authService.register(name, username, password);

        if (authCode != null) {
            Map<String, String> response = new HashMap<>();
            response.put("authToken", authCode.getCode());
            ResponseEntity.ok(response);
        } else {
            // Return 409 (conflict) if the account already exists
            ResponseEntity.status(409).build();
        }
    }

    @PostMapping("/logout")
    void logout(@RequestBody Map<String, String> body) {
        // Get the request parameters
        String username = body.get("username");
        String refreshToken = body.get("refreshToken");

        boolean success = authService.logout(username, refreshToken);
        if (success) {
            ResponseEntity.ok().build();
        } else {
            ResponseEntity.badRequest().build();
        }
    }

}
