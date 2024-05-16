package com.fosanzdev.trainingBrainAPI.services.data;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.models.details.User;
import com.fosanzdev.trainingBrainAPI.repositories.data.UserRepository;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IAccountService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IUserDataService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDataService implements IUserDataService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IAccountService accountService;

    @Transactional
    @Override
    public User getUserByAccountID(String accountID) {
        return userRepository.findById(accountID).orElse(null);
    }

    @Transactional
    @Override
    public User getUserByAccessToken(String accessToken) {
        Account account = accountService.getAccountByAccessToken(accessToken);
        return getUserByAccountID(account.getId());
    }

    @Override
    public void updateUser(User user) {
        userRepository.save(user);
    }
}
