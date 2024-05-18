package com.fosanzdev.trainingBrainAPI.services.data;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.models.details.Professional;
import com.fosanzdev.trainingBrainAPI.repositories.auth.AccountRepository;
import com.fosanzdev.trainingBrainAPI.repositories.data.ProfessionalRepository;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IAccountService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IProDataService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProDataService implements IProDataService {

    @Autowired
    private ProfessionalRepository professionalRepository;

    //TODO: Use IAccountService instead of AccountRepository
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private IAccountService accountService;

    @Transactional
    @Override
    public Professional getProfessionalByAccountId(String accountID) {
        Account account = accountService.getAccountById(accountID);
        if (account != null && account.isVerified() && account.isProfessional())
            return account.getProfessionalDetails();
        else
            return null;
    }

    @Transactional
    @Override
    public Professional getProfessionalByAccessToken(String accessToken) {
        Account account = accountService.getAccountByAccessToken(accessToken);
        return getProfessionalByAccountId(account.getId());
    }

    @Override
    public void updateProfessional(Professional proToUpdate, Professional updatedProfessional) {
        if (updatedProfessional.getPublic_bio() != null)
            proToUpdate.setPublic_bio(updatedProfessional.getPublic_bio());

        if (updatedProfessional.getWorkTitle() != null)
            proToUpdate.setWorkTitle(updatedProfessional.getWorkTitle());

        professionalRepository.save(proToUpdate);
    }

    @Transactional
    @Override
    public void createProfessionalIfNotExists(String accountID) {
        Account account = accountService.getAccountById(accountID);
        if (account.getProfessionalDetails() == null){
            //Create professional details
            Professional professional = new Professional();

            //Set professional details
            professional.setAccount(account);
            account.setProfessionalDetails(professional);

            //Save to database
            professionalRepository.save(professional);
        }
    }
}
