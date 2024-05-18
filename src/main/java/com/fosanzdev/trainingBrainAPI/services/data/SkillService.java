package com.fosanzdev.trainingBrainAPI.services.data;

import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.models.details.ProfessionalSkill;
import com.fosanzdev.trainingBrainAPI.models.details.Skill;
import com.fosanzdev.trainingBrainAPI.repositories.data.ProfessionalSkillRepository;
import com.fosanzdev.trainingBrainAPI.repositories.data.SkillRepository;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.ISkillService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillService implements ISkillService {

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private ProfessionalSkillRepository professionalSkillRepository;

    @Override
    public List<Skill> getAll() {
        return skillRepository.findAll();
    }

    @Override
    public Skill getSkillById(Long skillId) {
        return skillRepository.findById(skillId).orElse(null);
    }

    @Transactional
    @Override
    public boolean addNewSkill(Professional professional, Skill skillId, int level) {
        try{
            for (ProfessionalSkill proSkills : professional.getProfessionalSkills()) {
                if (proSkills.getSkill().getId().equals(skillId.getId())) {
                    return false;
                }
            }
            ProfessionalSkill professionalSkill = new ProfessionalSkill();
            professionalSkill.setProfessional(professional);
            professionalSkill.setSkill(skillId);
            professionalSkill.setLevel(level);
            professionalSkillRepository.save(professionalSkill);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteSkill(Professional professional, Skill skill) {
        try {
            for (ProfessionalSkill proSkills : professional.getProfessionalSkills()) {
                if (proSkills.getSkill().getId().equals(skill.getId())) {
                    professionalSkillRepository.delete(proSkills);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateSkill(Professional professional, Skill skill, int level) {
        try {
            for (ProfessionalSkill proSkills : professional.getProfessionalSkills()) {
                if (proSkills.getSkill().getId().equals(skill.getId())) {
                    proSkills.setLevel(level);
                    professionalSkillRepository.save(proSkills);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
