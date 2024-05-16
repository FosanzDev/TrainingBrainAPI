package com.fosanzdev.trainingBrainAPI.repositories.data;

import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessionalRepository extends JpaRepository<Professional, String> {
}
