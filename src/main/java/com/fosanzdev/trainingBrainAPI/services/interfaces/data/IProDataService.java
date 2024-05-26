package com.fosanzdev.trainingBrainAPI.services.interfaces.data;

import com.fosanzdev.trainingBrainAPI.models.data.Professional;

public interface IProDataService {

    Professional getProfessionalById(String professionalId);
    Professional getProfessionalByAccountId(String accountID);
    Professional getProfessionalByAccessToken(String accessToken);
    void updateProfessional(Professional user, Professional updatedProfessional);
    boolean setWorkTitle(Professional professional, Long workTitleId);
    void createProfessionalIfNotExists(String accountID);
}
