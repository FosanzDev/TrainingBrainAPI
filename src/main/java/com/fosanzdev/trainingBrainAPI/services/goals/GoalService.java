package com.fosanzdev.trainingBrainAPI.services.goals;

import com.fosanzdev.trainingBrainAPI.models.data.User;
import com.fosanzdev.trainingBrainAPI.models.goals.Goal;
import com.fosanzdev.trainingBrainAPI.models.goals.GoalEntry;
import com.fosanzdev.trainingBrainAPI.repositories.goals.GoalEntryRepository;
import com.fosanzdev.trainingBrainAPI.repositories.goals.GoalRepository;
import com.fosanzdev.trainingBrainAPI.services.interfaces.goals.IGoalService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class GoalService implements IGoalService {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private GoalEntryRepository goalEntryRepository;


    @Transactional
    @Override
    public boolean addGoal(Goal goal, User user) {

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime startOfDay = now.toLocalDate().atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime sevenDaysFromNow = now.plusDays(7);

        ZonedDateTime earliestStart = startOfDay.minusHours(14); // Adjust for maximum positive time zone offset
        ZonedDateTime latestStart = sevenDaysFromNow.plusHours(12); // Adjust for maximum negative time zone offset

        if (goal.getStartDateTime().atZone(ZoneOffset.UTC).isBefore(earliestStart) ||
                goal.getStartDateTime().atZone(ZoneOffset.UTC).isAfter(latestStart)) {
            return false;
        }

        goal.setUser(user);
        goal.setCompleted(false);
        goalRepository.save(goal);
        return true;
    }

    @Override
    public boolean markGoalAsDone(Goal goal) {
        if (goal.isCompleted()) {
            return false;
        }

        if (pendingToday(goal)) {
            goalEntryRepository.save(new GoalEntry(goal));
            if (goalEntryRepository.findAllByGoalId(goal.getId()).size() >= goal.getRepetitions()) {
                goal.setCompleted(true);
                goalRepository.save(goal);
            }
            return true;
        }

        return false;
    }

    @Override
    public Goal getGoal(User user, String id) {
        Goal goal = goalRepository.findById(id).orElse(null);
        if (goal != null && goal.getUser().getId().equals(user.getId())) {
            return goal;
        }
        return null;
    }

    @Override
    public List<Goal> getAllGoals(User user) {
        return goalRepository.findAllByUserId(user.getId());
    }

    @Transactional
    @Override
    public boolean deleteGoal(User user, String id) {
        Goal goal = goalRepository.findById(id).orElse(null);
        if (goal != null && goal.getUser().getId().equals(user.getId())) {
            List<GoalEntry> entries = goalEntryRepository.findAllByGoalId(goal.getId());
            goalEntryRepository.deleteAll(entries);
            goalRepository.delete(goal);
            return true;
        }
        return false;
    }

    @Override
    public boolean pendingToday(Goal goal) {
        List<GoalEntry> entries = goalEntryRepository.findAllByGoalId(goal.getId());
        if (entries.isEmpty()) {
            return true;
        }

        Instant lastEntryTime = entries.get(0).getSubmissionDateTime();
        Instant nextDueTime = lastEntryTime.plus(goalRepository.findById(goal.getId()).get().getHoursBetween(), ChronoUnit.HOURS);
        return Instant.now().isAfter(nextDueTime);
    }
}
