package com.fosanzdev.trainingBrainAPI.models.details;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String publicBio;
    private String privateBio;
    private String history;

    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_account", referencedColumnName = "id")
    private Account account;

    public Map<String, String> toMap() {
        return Map.of(
                "id", id,
                "public_bio", publicBio,
                "private_bio", privateBio,
                "history", history,
                "date_of_birth", dateOfBirth.toString(),
                "account_id", account.getId()
        );
    }

    public Map<String, String> toPublicMap() {
        return Map.of(
                "id", id,
                "public_bio", publicBio,
                "date_of_birth", dateOfBirth.toString()
        );
    }
}
