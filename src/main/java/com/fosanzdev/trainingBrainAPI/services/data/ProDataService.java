package com.fosanzdev.trainingBrainAPI.services.data;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.models.data.WorkTitle;
import com.fosanzdev.trainingBrainAPI.repositories.data.ProfessionalRepository;
import com.fosanzdev.trainingBrainAPI.repositories.data.WorkTitleRepository;
import com.fosanzdev.trainingBrainAPI.services.interfaces.auth.IAccountService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IProDataService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProDataService implements IProDataService {

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private WorkTitleRepository workTitleRepository;

    @Override
    public Professional getProfessionalById(String professionalId) {
        return professionalRepository.findById(professionalId).orElse(null);
    }

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
        if (updatedProfessional.getPublicBio() != null)
            proToUpdate.setPublicBio(updatedProfessional.getPublicBio());

        professionalRepository.save(proToUpdate);
    }

    @Override
    public boolean setWorkTitle(Professional professional, Long workTitleId) {
        WorkTitle workTitle = workTitleRepository.findById(workTitleId).orElse(null);
        if (workTitle != null){
            professional.setWorkTitle(workTitle);
            professionalRepository.save(professional);
            return true;
        } else {
            return false;
        }
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
