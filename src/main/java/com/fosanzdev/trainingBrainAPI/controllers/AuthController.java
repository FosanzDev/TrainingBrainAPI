package com.fosanzdev.trainingBrainAPI.controllers;

import com.fosanzdev.trainingBrainAPI.services.auth.interfaces.IAuthService;
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
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/login")
    ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
        //Get username and password from body
        String username = body.get("username");
        String password = body.get("password");

        Map<String, String> response = new HashMap<>();
        response.put("access_token", "token" + username + password);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    void verify(@RequestBody Map<String, String> body) {
    }

    @PostMapping("/refresh")
    void refresh(@RequestBody Map<String, String> body) {
    }

    @PostMapping("/register")
    void register(@RequestBody Map<String, String> body) {
    }

    @PostMapping("/logout")
    void logout(@RequestBody Map<String, String> body) {
    }

}
