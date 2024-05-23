package com.fosanzdev.trainingBrainAPI.services.appointments;

import com.fosanzdev.trainingBrainAPI.models.appointments.ProfessionalHoliday;
import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.repositories.appointments.ProfessionalHolidaysRepository;
import com.fosanzdev.trainingBrainAPI.services.interfaces.appointments.IProHolidaysService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ProHolidaysService implements IProHolidaysService {

    @Autowired
    private ProfessionalHolidaysRepository professionalHolidaysRepository;

    @Override
    public List<ProfessionalHoliday> findByProfessionalId(String professionalId) {
        return professionalHolidaysRepository.findByProfessionalId(professionalId);
    }

    @Transactional
    @Override
    public boolean addHoliday(Professional professional, ProfessionalHoliday holiday) {
        if (holiday.getStartDateTime() == null || holiday.getEndDateTime() == null) {
            return false;
        }

        if (!holiday.getStartDateTime().isBefore(holiday.getEndDateTime())) {
            return false;
        }

        Instant now = Instant.now();
        if (holiday.getStartDateTime().isBefore(now) || holiday.getEndDateTime().isBefore(now)) {
            return false;
        }

        List<ProfessionalHoliday> activeHolidays = professionalHolidaysRepository.findByProfessionalId(professional.getId());
        if (hasConflicts(activeHolidays, holiday)) return false;

        holiday.setProfessional(professional);
        professionalHolidaysRepository.save(holiday);
        return true;
    }

    public boolean hasConflicts(List<ProfessionalHoliday> activeHolidays, ProfessionalHoliday newHoliday) {
        for (ProfessionalHoliday activeHoliday : activeHolidays) {
            if (!newHoliday.getStartDateTime().isAfter(activeHoliday.getEndDateTime()) &&
                    !newHoliday.getEndDateTime().isBefore(activeHoliday.getStartDateTime())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteHoliday(String holidayId) {
        return false;
    }
}
