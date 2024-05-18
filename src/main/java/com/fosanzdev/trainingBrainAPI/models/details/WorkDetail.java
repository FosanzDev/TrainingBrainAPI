package com.fosanzdev.trainingBrainAPI.models.details;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "work_details")
public class WorkDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private String startDate;
    private String endDate;
    private String enterprise;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_work_title", referencedColumnName = "id")
    private WorkTitle workTitle;

    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_professional", referencedColumnName = "id")
    private Professional professional;

    public Map<String, String> toMap(){
        return Map.of(
            "id", id.toString(),
            "description", description,
            "start_date", startDate,
            "end_date", endDate,
            "enterprise", enterprise,
            "workTitle", workTitle.toMap().toString()
        );
    }
}
