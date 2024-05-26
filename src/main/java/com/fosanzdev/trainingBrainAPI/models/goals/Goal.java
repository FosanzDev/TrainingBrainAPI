package com.fosanzdev.trainingBrainAPI.models.goals;

import com.fosanzdev.trainingBrainAPI.models.data.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 100)
    private String title;
    @Column(length = 1600)
    private String description;

    private boolean completed;
    private int hoursBetween;
    private int repetitions;

    private Instant startDateTime;

    @ManyToOne
    @JoinColumn(name = "fk_user", referencedColumnName = "id")
    private User user;


    public static Goal fromMap(Map<String, Object> map){
        try{
            Goal goal = new Goal();
            goal.setTitle((String) map.get("title"));
            goal.setDescription((String) map.get("description"));
            goal.setHoursBetween((int) map.get("hoursBetween"));
            goal.setRepetitions((int) map.get("repetitions"));
            goal.setStartDateTime(Instant.parse((String) map.get("startDateTime")));
            return goal;
        } catch (Exception e){
            return null;
        }
    }

    public Map<String, Object> toMap(){
        return Map.of(
                "id", id,
                "title", title,
                "description", description,
                "completed", completed,
                "hoursBetween", hoursBetween,
                "repetitions", repetitions,
                "startDateTime", startDateTime.toString()
        );
    }
}
