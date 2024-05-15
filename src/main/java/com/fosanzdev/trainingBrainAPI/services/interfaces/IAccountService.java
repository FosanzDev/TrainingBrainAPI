package com.fosanzdev.trainingBrainAPI.services.interfaces;

import com.fosanzdev.trainingBrainAPI.models.Account;

public interface IAccountService {

    public Account getAccount(String username);
    public Account getAccountByAccessToken(String accessToken);
}
