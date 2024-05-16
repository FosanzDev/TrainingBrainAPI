package com.fosanzdev.trainingBrainAPI.models.details;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String start_date;
    private String end_date;
    private String enterprise;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_work_title", referencedColumnName = "id")
    private WorkTitle workTitle;

    @OneToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_professional", referencedColumnName = "id")
    private Professional professional;
}
