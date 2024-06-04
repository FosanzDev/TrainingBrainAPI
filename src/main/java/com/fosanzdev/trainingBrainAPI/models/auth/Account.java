package com.fosanzdev.trainingBrainAPI.models.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.models.data.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
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

    @JsonIgnore
    @OneToOne(mappedBy = "account")
    private Professional professionalDetails;

    @JsonIgnore
    @OneToOne(mappedBy = "account")
    private User userDetails;

    public Map<String, Object> toMap() {
        String relatedId = professional ? professionalDetails.getId() : userDetails.getId();

        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("email", email);
        map.put("username", username);
        map.put("isProfessional", professional);
        map.put(professional ? "professionalId" : "userId", relatedId);
        map.put("isVerified", verified);
        return map;
    }

    public Map<String, Object> toBasicMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("email", email);
        map.put("username", username);
        map.put("isProfessional", professional);
        map.put(professional ? "professionalId" : "userId", professional ? professionalDetails.getId() : userDetails.getId());
        return map;
    }
}