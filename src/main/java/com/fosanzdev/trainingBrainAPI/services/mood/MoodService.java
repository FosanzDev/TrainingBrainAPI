package com.fosanzdev.trainingBrainAPI.services.mood;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.models.mood.AccountMood;
import com.fosanzdev.trainingBrainAPI.models.mood.Mood;
import com.fosanzdev.trainingBrainAPI.repositories.auth.AccountRepository;
import com.fosanzdev.trainingBrainAPI.repositories.mood.AccountMoodRepository;
import com.fosanzdev.trainingBrainAPI.repositories.mood.MoodRepository;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IMoodService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoodService implements IMoodService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MoodRepository moodRepository;

    @Autowired
    private AccountMoodRepository accountMoodRepository;

    @Transactional
    @Override
    public void addEntry(String accountId, String moodId) {
        Account account = accountRepository.findById(accountId).orElse(null);
        Mood mood = moodRepository.findById(moodId).orElse(null);
        if (account != null && mood != null) {
            AccountMood accountMood = new AccountMood();
            accountMood.setAccount(account);
            accountMood.setMood(mood);
            accountMoodRepository.save(accountMood);
        }
    }

    @Override
    public void removeEntry(AccountMood accountMood) {
        accountMoodRepository.delete(accountMood);
    }

    @Override
    public void updateEntry(AccountMood accountMood) {
        accountMoodRepository.save(accountMood);
    }

    @Override
    public AccountMood getEntry(Long id) {
        return accountMoodRepository.findById(id).orElse(null);
    }

    @Override
    public List<Mood> getMoods() {
        return moodRepository.findAll();
    }
    @Override
    public List<AccountMood> getHistory(String accountId, Integer limit, Integer offset) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        if (offset == null || offset < 0) {
            offset = 0;
        }
        return accountMoodRepository.findByAccountWithLimit(accountId, limit, offset);
    }
}
