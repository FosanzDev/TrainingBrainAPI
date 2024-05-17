package com.fosanzdev.trainingBrainAPI.services.interfaces;

import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.models.details.User;

public interface IProDataService {

    Professional getProfessionalByAccountId(String accountID);
    Professional getProfessionalByAccessToken(String accessToken);
    void updateProfessional(Professional user, Professional updatedProfessional);
    void createProfessionalIfNotExists(String accountID);
}
