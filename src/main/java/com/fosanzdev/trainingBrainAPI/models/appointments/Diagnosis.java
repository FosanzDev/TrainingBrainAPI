package com.fosanzdev.trainingBrainAPI.models.appointments;

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
@Table(name = "diagnosis")
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 100)
    private String header;

    @Column(length = 400)
    private String shortDescription;

    @Column(length = 1600)
    private String description;

    @Column(length = 1600)
    private String recommendation;

    @Column(length = 1600)
    private String treatment;

    @OneToOne(mappedBy = "diagnosis", cascade = {CascadeType.REFRESH})
    private Appointment appointment;

    public static Diagnosis fromMap(Map<String, Object> jsonMap) {
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setHeader((String) jsonMap.get("header"));
        diagnosis.setShortDescription((String) jsonMap.get("shortDescription"));
        diagnosis.setDescription((String) jsonMap.get("description"));
        diagnosis.setRecommendation((String) jsonMap.get("recommendation"));
        diagnosis.setTreatment((String) jsonMap.get("treatment"));
        return diagnosis;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("header", header);
        map.put("shortDescription", shortDescription);
        map.put("description", description);
        map.put("recommendation", recommendation);
        map.put("treatment", treatment);
        return map;
    }
}
