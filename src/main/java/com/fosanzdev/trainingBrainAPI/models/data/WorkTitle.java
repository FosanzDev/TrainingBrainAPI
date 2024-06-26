package com.fosanzdev.trainingBrainAPI.models.data;

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
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("branch", branch.toMap());
        return map;
    }
}
