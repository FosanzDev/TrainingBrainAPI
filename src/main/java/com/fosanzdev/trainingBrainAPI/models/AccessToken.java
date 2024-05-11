package com.fosanzdev.trainingBrainAPI.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "access_tokens")
public class AccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String token;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_account", referencedColumnName = "id")
    private Account account;
}