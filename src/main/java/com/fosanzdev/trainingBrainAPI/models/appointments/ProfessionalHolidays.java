package com.fosanzdev.trainingBrainAPI.models.appointments;

import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "professional_holidays")
public class ProfessionalHolidays {

    private enum HolidayType {
        VACATION,
        SICK_LEAVE,
        OTHER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private HolidayType holidayType;
    private String description;
    private Instant startDateTime;
    private Instant endDateTime;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_professional", referencedColumnName = "id")
    private Professional professional;
}
