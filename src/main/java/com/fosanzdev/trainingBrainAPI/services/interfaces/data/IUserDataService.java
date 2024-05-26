package com.fosanzdev.trainingBrainAPI.services.interfaces.data;

import com.fosanzdev.trainingBrainAPI.models.data.User;

import java.text.ParseException;

public interface IUserDataService {

    User getUserByAccountID(String accountID);
    User getUserByAccessToken(String accessToken);
    void updateUser(User userToUpdate, User updatedUser) throws ParseException;
    void createUserIfNotExists(String accountID);
}
