package com.fosanzdev.trainingBrainAPI.services.interfaces.goals;

import com.fosanzdev.trainingBrainAPI.models.details.User;
import com.fosanzdev.trainingBrainAPI.models.goals.Routine;

import java.util.List;

public interface IRoutineService {

    List<Routine> getTodayRoutines(User user);
    boolean addRoutine(Routine routine, User user);
    boolean markRoutineAsDone(Routine routine);
    Routine getRoutine(User user, String id);
    List<Routine> getAllRoutines(User user);
    boolean deleteRoutine(User user, String id);
    boolean pendingToday(String id);
}
