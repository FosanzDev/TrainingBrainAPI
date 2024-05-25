package com.fosanzdev.trainingBrainAPI.repositories.goals;

import com.fosanzdev.trainingBrainAPI.models.goals.GoalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalEntryRepository extends JpaRepository<GoalEntry, String>{
}
