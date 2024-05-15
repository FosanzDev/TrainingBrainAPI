package com.fosanzdev.trainingBrainAPI.models.mood;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    public Mood(String name, String description, String color) {
        this.name = name;
        this.description = description;
        this.color = color;
    }

    @OneToMany(mappedBy = "mood", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AccountMood> accountMoods;
}
