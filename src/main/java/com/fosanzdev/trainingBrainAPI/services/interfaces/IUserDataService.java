package com.fosanzdev.trainingBrainAPI.services.interfaces;

import com.fosanzdev.trainingBrainAPI.models.details.User;

public interface IUserDataService {

    User getUserByAccountID(String accountID);
    User getUserByAccessToken(String accessToken);
    void updateUser(User user);
    void createUserIfNotExists(String accountID);
}
