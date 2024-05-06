package com.fosanzdev.trainingBrainAPI.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String password;

    @OneToOne(mappedBy = "account", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private RefreshToken refreshToken;

    @OneToOne(mappedBy = "account", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private AccessToken accessToken;

    @OneToOne(mappedBy = "account", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private AuthCode authCode;
}