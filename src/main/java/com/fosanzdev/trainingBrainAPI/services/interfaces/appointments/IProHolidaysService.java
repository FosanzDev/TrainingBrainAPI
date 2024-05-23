package com.fosanzdev.trainingBrainAPI.services.interfaces.appointments;

import com.fosanzdev.trainingBrainAPI.models.appointments.ProfessionalHoliday;
import com.fosanzdev.trainingBrainAPI.models.details.Professional;

import java.util.List;

public interface IProHolidaysService {
    List<ProfessionalHoliday> findByProfessionalId(String professionalId);
    boolean addHoliday(Professional professional, ProfessionalHoliday holiday);
    boolean deleteHoliday(String holidayId);

}
