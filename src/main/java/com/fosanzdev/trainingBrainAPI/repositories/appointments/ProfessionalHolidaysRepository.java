package com.fosanzdev.trainingBrainAPI.repositories.appointments;

import com.fosanzdev.trainingBrainAPI.models.appointments.ProfessionalHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProfessionalHolidaysRepository extends JpaRepository<ProfessionalHoliday, String> {

    @Query("SELECT ph FROM ProfessionalHoliday ph WHERE ph.professional.id = ?1 AND ph.endDateTime > CURRENT_TIMESTAMP")
    List<ProfessionalHoliday> findByProfessionalId(String professionalId);
}
