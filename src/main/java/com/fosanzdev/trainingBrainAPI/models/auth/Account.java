package com.fosanzdev.trainingBrainAPI.models.auth;

import com.fosanzdev.trainingBrainAPI.models.mood.AccountMood;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String username;
    private String name;
    private String password;
    private boolean professional;
    private boolean verified;

    @OneToMany(mappedBy = "account", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    private List<RefreshToken> refreshToken;

    @OneToMany(mappedBy = "account", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    private List<AccessToken> accessToken;

    @OneToMany(mappedBy = "account", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    private List<AuthCode> authCode;

    @OneToMany(mappedBy = "account", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    private List<AccountMood> accountMoods;

    public Map<String, String> toMap(){
        return Map.of(
            "id", id,
            "name", name,
            "username", username,
            "isProfessional", String.valueOf(professional),
            "isVerified", String.valueOf(verified)
        );
    }

    public Map<String, String> toBasicMap(){
        return Map.of(
            "id", id,
            "username", username
        );
    }
}