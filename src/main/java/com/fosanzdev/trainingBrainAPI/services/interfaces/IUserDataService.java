package com.fosanzdev.trainingBrainAPI.services.interfaces;

import com.fosanzdev.trainingBrainAPI.models.details.User;

public interface IUserDataService {

    public User getUserByAccountID(String accountID);
    public User getUserByAccessToken(String accessToken);
    public void updateUser(User user);
    public void createUserIfNotExists(String accountID);
}
