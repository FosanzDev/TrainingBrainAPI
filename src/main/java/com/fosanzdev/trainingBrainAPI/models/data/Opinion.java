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
@Table(name = "opinions")
public class Opinion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 100)
    private String title;

    @Column(length = 1600)
    private String description;

    private int rating;

    @ManyToOne
    @JoinColumn(name = "fk_user", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "fk_professional", referencedColumnName = "id")
    private Professional professional;

    public static Opinion fromMap(Map<String, Object> jsonData) {
        try {
            Opinion opinion = new Opinion();
            opinion.setTitle((String) jsonData.get("title"));
            opinion.setDescription((String) jsonData.get("description"));
            opinion.setRating((int) jsonData.get("rating"));
            return opinion;
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("description", description);
        map.put("rating", rating);
        return map;
    }
}
