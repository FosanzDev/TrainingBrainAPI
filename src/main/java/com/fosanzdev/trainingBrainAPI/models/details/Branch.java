package com.fosanzdev.trainingBrainAPI.models.details;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "work_branches")
public class Branch {

    @Id
    private Long id;
    private String name;
}
