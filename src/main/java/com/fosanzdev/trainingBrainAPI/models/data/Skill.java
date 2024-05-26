package com.fosanzdev.trainingBrainAPI.models.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    public Skill(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Map<String, String> toMap(){
        return Map.of(
            "name", name,
            "description", description
        );
    }
}
