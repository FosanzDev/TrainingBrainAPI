package com.fosanzdev.trainingBrainAPI.services.data;

import com.fosanzdev.trainingBrainAPI.models.data.Opinion;
import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.models.data.User;
import com.fosanzdev.trainingBrainAPI.repositories.data.OpinionRepository;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IOpinionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpinionService implements IOpinionService {

    @Autowired
    private OpinionRepository opinionRepository;


    @Transactional
    @Override
    public boolean createOpinion(User user, Professional professional, Opinion opinion) {
        Opinion lastOpinion = opinionRepository.findOpinionByUserAndProfessional(user.getId(), professional.getId());
        if (lastOpinion != null) return false;

        if (opinion.getRating() < 1 || opinion.getRating() > 10) return false;
        if (opinion.getTitle().length() < 5 || opinion.getTitle().length() > 100) return false;
        if (opinion.getDescription().length() < 5 || opinion.getDescription().length() > 1600) return false;

        opinion.setUser(user);
        opinion.setProfessional(professional);
        opinionRepository.save(opinion);
        return true;
    }

    @Override
    public boolean deleteOpinion(User user, String id) {
        try{
            if (!opinionRepository.findById(id).get().getUser().getId().equals(user.getId())) return false;

            opinionRepository.deleteById(id);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    @Override
    public List<Opinion> getMyOpinions(User user) {
        return opinionRepository.getByUserId(user.getId());
    }

    @Override
    public List<Opinion> getMyOpinions(Professional professional) {
        return opinionRepository.getByProfessionalId(professional.getId());
    }

    @Override
    public List<Opinion> getOpinionsByProfessional(Professional professional) {
        return opinionRepository.getByProfessionalId(professional.getId());
    }
}
