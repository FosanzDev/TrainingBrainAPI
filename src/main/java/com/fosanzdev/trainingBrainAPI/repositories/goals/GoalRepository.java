package com.fosanzdev.trainingBrainAPI.repositories.goals;

import com.fosanzdev.trainingBrainAPI.models.goals.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, String>{
}
