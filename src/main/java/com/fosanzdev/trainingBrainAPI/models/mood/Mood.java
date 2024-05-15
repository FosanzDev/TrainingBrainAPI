package com.fosanzdev.trainingBrainAPI.models.mood;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "moods")
public class Mood {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String description;
    private String color;
    private String icon;

    public Mood(String name, String description, String color, String icon) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.icon = icon;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "mood", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AccountMood> accountMoods;
}
