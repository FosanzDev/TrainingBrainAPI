package com.fosanzdev.trainingBrainAPI.models.goals;

import com.fosanzdev.trainingBrainAPI.models.details.User;
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
@Table(name = "routines")
public class Routine {

    public enum RoutineType {
        DAYS,
        WEEKS,
        MONTHS
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 100)
    private String title;
    @Column(length = 1600)
    private String description;

    private int every;
    private RoutineType routineType;

    private Instant startTime;
    private Instant endTime;
    private Instant submissionDate;

    @ManyToOne
    @JoinColumn(name = "fk_user", referencedColumnName = "id")
    private User user;

    public static Routine fromMap(Map<String, Object> jsonData){
        try {
            Routine routine = new Routine();
            routine.setTitle((String) jsonData.get("title"));
            routine.setDescription((String) jsonData.get("description"));
            routine.setEvery((int) jsonData.get("every"));
            routine.setRoutineType(RoutineType.valueOf((String) jsonData.get("routineType")));
            routine.setStartTime(Instant.parse((String) jsonData.get("startTime")));
            routine.setEndTime(Instant.parse((String) jsonData.get("endTime")));
            routine.setSubmissionDate(Instant.now());
            return routine;
        } catch (Exception e){
            return null;
        }
    }

    public Map<String, Object> toMap(){
        return Map.of(
                "id", id,
                "title", title,
                "description", description,
                "every", every,
                "routineType", routineType,
                "startTime", startTime,
                "endTime", endTime,
                "submissionDate", submissionDate
        );
    }
}
