package com.fosanzdev.trainingBrainAPI.models.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "professional_skills")
public class ProfessionalSkill {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "fk_professional", referencedColumnName = "id")
    private Professional professional;

    @ManyToOne
    @JoinColumn(name = "fk_skill", referencedColumnName = "id")
    private Skill skill;

    private int level;

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("skill", skill.toMap());
        map.put("level", level);
        return map;
    }
}