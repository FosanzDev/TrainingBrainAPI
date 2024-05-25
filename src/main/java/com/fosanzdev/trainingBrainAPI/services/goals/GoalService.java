package com.fosanzdev.trainingBrainAPI.services.goals;

import com.fosanzdev.trainingBrainAPI.models.details.User;
import com.fosanzdev.trainingBrainAPI.models.goals.Goal;
import com.fosanzdev.trainingBrainAPI.models.goals.GoalEntry;
import com.fosanzdev.trainingBrainAPI.repositories.data.UserRepository;
import com.fosanzdev.trainingBrainAPI.repositories.goals.GoalEntryRepository;
import com.fosanzdev.trainingBrainAPI.repositories.goals.GoalRepository;
import com.fosanzdev.trainingBrainAPI.services.interfaces.goals.IGoalService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class GoalService implements IGoalService {

    @Autowired
    private UserRepository userRepository;

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
            System.out.println("Invalid start date");
            System.out.println("Start date: " + goal.getStartDateTime());
            System.out.println("Earliest start: " + earliestStart);
            System.out.println("Latest start: " + latestStart);
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

        if (pendingToday(goal.getId())) {
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

    @Override
    public boolean deleteGoal(User user, String id) {
        Goal goal = goalRepository.findById(id).orElse(null);
        if (goal != null && goal.getUser().getId().equals(user.getId())) {
            goalRepository.delete(goal);
            return true;
        }
        return false;
    }

    @Override
    public boolean pendingToday(String id) {
        List<GoalEntry> entries = goalEntryRepository.findAllByGoalId(id);
        if (entries.isEmpty()) {
            return true;
        }

        Instant lastEntryTime = entries.get(0).getSubmissionDateTime();
        Instant nextDueTime = lastEntryTime.plus(goalRepository.findById(id).get().getHoursBetween(), ChronoUnit.HOURS);
        return Instant.now().isAfter(nextDueTime);
    }
}
