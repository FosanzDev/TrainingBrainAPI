package com.fosanzdev.trainingBrainAPI.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    private String id;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @MapsId
    @JoinColumn(name = "fk_account", referencedColumnName = "id")
    private Account account;

    private String token;
}