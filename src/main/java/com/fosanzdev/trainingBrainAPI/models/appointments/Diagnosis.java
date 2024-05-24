package com.fosanzdev.trainingBrainAPI.models.appointments;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String header;
    private String shortDescription;
    private String description;

    private String recommendation;
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
        return Map.of(
                "header", header,
                "shortDescription", shortDescription,
                "description", description,
                "recommendation", recommendation,
                "treatment", treatment
        );
    }
}
