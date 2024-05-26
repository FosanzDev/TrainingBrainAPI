package com.fosanzdev.trainingBrainAPI.services.interfaces.goals;

import com.fosanzdev.trainingBrainAPI.models.details.User;
import com.fosanzdev.trainingBrainAPI.models.goals.Goal;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IGoalService {

    boolean addGoal(Goal goal, User user);
    boolean markGoalAsDone(Goal goal);
    Goal getGoal(User user, String id);
    List<Goal> getAllGoals(User user);
    boolean deleteGoal(User user, String id);
    boolean pendingToday(Goal goal);
}
