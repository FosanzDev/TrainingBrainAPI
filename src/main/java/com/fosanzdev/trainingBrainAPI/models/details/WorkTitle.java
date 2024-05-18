package com.fosanzdev.trainingBrainAPI.models.details;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "work_titles")
public class WorkTitle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "fk_branch", referencedColumnName = "id")
    private Branch branch;

    public WorkTitle(String title, Branch branch) {
        this.title = title;
        this.branch = branch;
    }

    public Map<String, Object> toMap(){
        return Map.of(
                "id", id,
            "title", title,
            "branch", branch.toMap()
        );
    }
}
