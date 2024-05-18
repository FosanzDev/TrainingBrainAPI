package com.fosanzdev.trainingBrainAPI.services.interfaces;

import com.fosanzdev.trainingBrainAPI.models.details.User;

import java.text.ParseException;

public interface IUserDataService {

    User getUserByAccountID(String accountID);
    User getUserByAccessToken(String accessToken);
    void updateUser(User userToUpdate, User updatedUser) throws ParseException;
    void createUserIfNotExists(String accountID);
}
