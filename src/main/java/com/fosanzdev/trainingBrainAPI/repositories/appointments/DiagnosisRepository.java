package com.fosanzdev.trainingBrainAPI.repositories.appointments;

import com.fosanzdev.trainingBrainAPI.models.appointments.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, String> {
}
