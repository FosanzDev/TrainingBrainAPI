package com.fosanzdev.trainingBrainAPI.repositories.goals;

import com.fosanzdev.trainingBrainAPI.models.goals.RoutineEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineRepository extends JpaRepository<RoutineEntry, String> {
}
