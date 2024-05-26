package com.fosanzdev.trainingBrainAPI.services.interfaces.appointments;

import com.fosanzdev.trainingBrainAPI.models.appointments.ProfessionalSchedule;
import com.fosanzdev.trainingBrainAPI.models.data.Professional;

import java.util.List;

public interface IProScheduleService {

    boolean changeSchedule(Professional professional, List<ProfessionalSchedule> schedule);
    List<ProfessionalSchedule> findByProfessionalId(String professionalId);
}
