package com.fosanzdev.trainingBrainAPI.repositories.auth;

import com.fosanzdev.trainingBrainAPI.models.Account;
import com.fosanzdev.trainingBrainAPI.models.AuthCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AuthCodeRepository extends JpaRepository<AuthCode, Integer> {

    @Query("SELECT a FROM AuthCode a WHERE a.account.id = :account_id")
    List<AuthCode> findByAccount(@Param("account_id") String account_id);
}
