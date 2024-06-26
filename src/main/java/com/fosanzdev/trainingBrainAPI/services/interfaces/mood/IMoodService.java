package com.fosanzdev.trainingBrainAPI.services.interfaces.mood;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.models.mood.AccountMood;
import com.fosanzdev.trainingBrainAPI.models.mood.Mood;

import java.util.List;

public interface IMoodService {

    public void addEntry(String accountId, String moodId);
    public void removeEntry(AccountMood accountMood);
    public void updateEntry(AccountMood accountMood);
    public AccountMood getEntry(Long id);
    public List<Mood> getMoods();
    public List<AccountMood> getHistory(String accountId, Integer limit, Integer offset);
}
