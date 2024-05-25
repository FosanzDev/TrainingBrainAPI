package com.fosanzdev.trainingBrainAPI.repositories.goals;

import com.fosanzdev.trainingBrainAPI.models.goals.Goal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, String>{

    @Query("SELECT g FROM Goal g WHERE g.user.id = ?1 ORDER BY g.startDateTime DESC")
    List<Goal> findAllByUserId(String userId);
}
