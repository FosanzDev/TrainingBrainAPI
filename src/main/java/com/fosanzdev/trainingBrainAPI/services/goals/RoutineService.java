package com.fosanzdev.trainingBrainAPI.services.goals;

import com.fosanzdev.trainingBrainAPI.models.data.User;
import com.fosanzdev.trainingBrainAPI.models.goals.Routine;
import com.fosanzdev.trainingBrainAPI.repositories.goals.RoutineRepository;
import com.fosanzdev.trainingBrainAPI.services.interfaces.goals.IRoutineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RoutineService implements IRoutineService {

    @Autowired
    private RoutineRepository routineRepository;


    @Override
    public List<Routine> getTodayRoutines(User user) {
        List<Routine> routines = routineRepository.findAllByUser(user.getId());
        routines.removeIf(routine -> !pendingToday(routine));
        return routines;
    }

    @Override
    public boolean addRoutine(Routine routine, User user) {
        routine.setUser(user);
        routineRepository.save(routine);
        return true;
    }

    @Override
    public Routine getRoutine(User user, String id) {
        return routineRepository.findById(id).orElse(null);
    }

    @Override
    public List<Routine> getAllRoutines(User user) {
        return routineRepository.findAllByUser(user.getId());
    }

    @Override
    public boolean deleteRoutine(User user, String id) {
        Routine routine = routineRepository.findById(id).orElse(null);
        if (routine == null || !routine.getUser().getId().equals(user.getId())) {
            return false;
        }
        routineRepository.delete(routine);
        return true;
    }

    @Override
    public boolean pendingToday(Routine routine) {
        Instant nextDueDate = routine.getSubmissionDate();

        // Calculate the next due date
        while (nextDueDate.isBefore(Instant.now())) {
            nextDueDate = switch (routine.getRoutineType()) {
                case DAYS -> nextDueDate.plus(routine.getEvery(), ChronoUnit.DAYS);
                case WEEKS -> nextDueDate.plus(routine.getEvery(), ChronoUnit.WEEKS);
                case MONTHS -> nextDueDate.plus(routine.getEvery(), ChronoUnit.MONTHS);
            };
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime startOfDay = now.minusHours(14); // Adjust for maximum positive time zone offset
        ZonedDateTime endOfDay = now.plusHours(12); // Adjust for maximum negative time zone offset

        return !nextDueDate.isBefore(startOfDay.toInstant()) && !nextDueDate.isAfter(endOfDay.toInstant());
    }
}
