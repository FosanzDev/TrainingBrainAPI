package com.fosanzdev.trainingBrainAPI.services.data;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.models.details.User;
import com.fosanzdev.trainingBrainAPI.repositories.auth.AccountRepository;
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


    //TODO: Use IAccountService instead of AccountRepository
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private IAccountService accountService;

    @Transactional
    @Override
    public User getUserByAccountID(String accountID) {
        Account account = accountService.getAccountById(accountID);
        if (account != null && account.isVerified() && !account.isProfessional())
            return account.getUserDetails();
        else
            return null;
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

    @Transactional
    @Override
    public void createUserIfNotExists(String accountID) {
        Account account = accountService.getAccountById(accountID);
        if (account.getUserDetails() == null){
            //Create user details
            User user = new User();

            //Set user details
            user.setAccount(account);
            account.setUserDetails(user);

            //Save to database
            userRepository.save(user);
        }
    }
}
