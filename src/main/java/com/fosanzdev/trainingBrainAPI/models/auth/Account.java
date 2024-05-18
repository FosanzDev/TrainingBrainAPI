package com.fosanzdev.trainingBrainAPI.models.auth;

import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.models.details.User;
import com.fosanzdev.trainingBrainAPI.models.mood.AccountMood;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
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
    private String email;
    private String password;
    private boolean professional;
    private boolean verified;

    @OneToOne(mappedBy = "account")
    private Professional professionalDetails;

    @OneToOne(mappedBy = "account")
    private User userDetails;

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>(Map.of(
                "id", id,
                "name", name,
                "email", email,
                "username", username,
                "isProfessional", professional,
                "isVerified", verified
        ));
        if (professionalDetails != null)
            map.put("professionalDetails", professionalDetails.toMap());
        if (userDetails != null)
            map.put("userDetails", userDetails.toMap());

        return map;
    }

    public Map<String, Object> toBasicMap(){
        return Map.of(
            "id", id,
            "name", name,
            "username", username
        );
    }
}