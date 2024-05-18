package com.fosanzdev.trainingBrainAPI.services.interfaces;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;

public interface IAccountService {

    public Account getAccountById(String id);
    public Account getAccountByUsername(String username);
    public Account getAccountByAccessToken(String accessToken);
    void updateAccount(Account accountToUpdate, Account updatedAccount);
}
