package com.fosanzdev.trainingBrainAPI.services.accounts;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.repositories.auth.AccessTokenRepository;
import com.fosanzdev.trainingBrainAPI.repositories.auth.AccountRepository;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements IAccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Override
    public Account getAccount(String username) {
        return accountRepository.findByUsername(username);
    }

    @Override
    public Account getAccountByAccessToken(String accessToken) {
        return accessTokenRepository.find(accessToken).getAccount();
    }
}
