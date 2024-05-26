package com.fosanzdev.trainingBrainAPI.services.interfaces.goals;

import com.fosanzdev.trainingBrainAPI.models.details.User;
import com.fosanzdev.trainingBrainAPI.models.goals.Routine;

public interface IRoutineService {

    boolean addRoutine(Routine routine, User user);
    boolean markRoutineAsDone(Routine routine);
    Routine getRoutine(User user, String id);
    boolean deleteRoutine(User user, String id);
    boolean pendingToday(String id);
}
