package com.fosanzdev.trainingBrainAPI.services.appointments;

import com.fosanzdev.trainingBrainAPI.models.appointments.ProfessionalSchedule;
import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.repositories.appointments.ProfessionalScheduleRepository;
import com.fosanzdev.trainingBrainAPI.services.interfaces.appointments.IProScheduleService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IProDataService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProScheduleService implements IProScheduleService {

    @Autowired
    private ProfessionalScheduleRepository professionalScheduleRepository;


    @Transactional
    @Override
    public boolean changeSchedule(Professional professional, List<ProfessionalSchedule> schedule) {
        List<ProfessionalSchedule> findByProfessionalId = professionalScheduleRepository.findByProfessionalId(professional.getId());
        if (findByProfessionalId != null) {
            professionalScheduleRepository.deleteAll(findByProfessionalId);
        }
        schedule.forEach(professionalSchedule -> professionalSchedule.setProfessional(professional));
        professionalScheduleRepository.saveAll(schedule);
        return true;
    }

    @Override
    public List<ProfessionalSchedule> findByProfessionalId(String professionalId) {
        return professionalScheduleRepository.findByProfessionalId(professionalId);
    }

}
