package com.fosanzdev.trainingBrainAPI.repositories.appointments;

import com.fosanzdev.trainingBrainAPI.models.appointments.ProfessionalSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProfessionalScheduleRepository extends JpaRepository<ProfessionalSchedule, String> {

    @Query("SELECT p FROM ProfessionalSchedule p WHERE p.professional.id = ?1")
    List<ProfessionalSchedule> findByProfessionalId(String professionalId);
}
