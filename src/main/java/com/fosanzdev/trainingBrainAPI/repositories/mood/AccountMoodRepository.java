package com.fosanzdev.trainingBrainAPI.repositories.mood;

import com.fosanzdev.trainingBrainAPI.models.mood.AccountMood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountMoodRepository extends JpaRepository<AccountMood, Long> {

    //NOTE: Using native query to avoid the use of the entity manager in order to use limit and offset
    @Query(value="SELECT * FROM account_moods WHERE fk_account = ?1 ORDER BY date DESC LIMIT ?2 OFFSET ?3", nativeQuery = true)
    List<AccountMood> findByAccountWithLimit(String accountId, int limit, int offset);

    @Query(value="SELECT * FROM account_moods WHERE fk_account = ?1", nativeQuery = true)
    List<AccountMood> findByAccount(String accountId);

}
