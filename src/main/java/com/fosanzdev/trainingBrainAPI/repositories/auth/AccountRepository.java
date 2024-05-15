package com.fosanzdev.trainingBrainAPI.repositories.auth;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, String>{


    @Query("SELECT a from Account a where a.username = :username")
    Account findByUsername(@Param("username") String username);

}
