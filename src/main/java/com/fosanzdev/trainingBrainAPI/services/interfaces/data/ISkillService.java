package com.fosanzdev.trainingBrainAPI.services.interfaces.data;

import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.models.data.Skill;

import java.util.List;

public interface ISkillService {
    List<Skill> getAll();
    Skill getSkillById(Long skillId);
    void addNewSkill(Professional professional, Skill skill, int level);
    boolean deleteSkill(Professional professional, Skill skill);
    boolean updateSkill(Professional professional, Skill skill, int level);
}
