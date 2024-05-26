package com.fosanzdev.trainingBrainAPI.services.interfaces.data;

import com.fosanzdev.trainingBrainAPI.models.data.Opinion;
import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.models.data.User;

import java.util.List;

public interface IOpinionService {

    boolean createOpinion(User user, Professional professional, Opinion opinion);
    boolean deleteOpinion(User user, String id);
    List<Opinion> getMyOpinions(Professional professional);
    List<Opinion> getMyOpinions(User user);
    List<Opinion> getOpinionsByProfessional(Professional professional);
}
