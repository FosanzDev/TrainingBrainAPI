package com.fosanzdev.trainingBrainAPI.repositories.goals;

import com.fosanzdev.trainingBrainAPI.models.goals.GoalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GoalEntryRepository extends JpaRepository<GoalEntry, String>{

    @Query("SELECT g FROM GoalEntry g WHERE g.goal.id = ?1 ORDER BY g.submissionDateTime DESC")
    List<GoalEntry> findAllByGoalId(String goalId);
}
