package com.fosanzdev.trainingBrainAPI.models.auth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "auth_codes")
public class AuthCode {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String code;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_account", referencedColumnName = "id")
    private Account account;
}