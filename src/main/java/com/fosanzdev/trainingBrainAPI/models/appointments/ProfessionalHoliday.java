package com.fosanzdev.trainingBrainAPI.models.appointments;

import com.fosanzdev.trainingBrainAPI.models.data.Professional;
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
@Table(name = "professional_holidays")
public class ProfessionalHoliday {

    private enum HolidayType {
        VACATION,
        SICK_LEAVE,
        OTHER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private HolidayType holidayType;

    @Column(length = 1000)
    private String description;
    private Instant startDateTime;
    private Instant endDateTime;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_professional", referencedColumnName = "id")
    private Professional professional;

    public static ProfessionalHoliday fromMap(Map<String, Object> jsonMap) {
        ProfessionalHoliday holiday = new ProfessionalHoliday();
        try {
            holiday.setHolidayType(HolidayType.valueOf((String) jsonMap.get("holidayType")));
            holiday.setDescription((String) jsonMap.get("description"));
            holiday.setStartDateTime(Instant.parse((String) jsonMap.get("startDateTime")));
            holiday.setEndDateTime(Instant.parse((String) jsonMap.get("endDateTime")));
        } catch (Exception e) {
            return null;
        }
        return holiday;
    }

    public Map<String, Object> toMap() {
        return Map.of(
                "id", id,
                "holidayType", holidayType.toString(),
                "description", description,
                "startDateTime", startDateTime.toString(),
                "endDateTime", endDateTime.toString()
        );
    }

    public Map<String, Object> toBasicMap() {
        return Map.of(
                "holidayType", holidayType.toString(),
                "startDateTime", startDateTime.toString(),
                "endDateTime", endDateTime.toString(),
                "description", description
        );
    }
}
