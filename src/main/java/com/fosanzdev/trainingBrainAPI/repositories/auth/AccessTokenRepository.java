package com.fosanzdev.trainingBrainAPI.repositories.auth;

import com.fosanzdev.trainingBrainAPI.models.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Integer> {

    @Query("SELECT a FROM AccessToken a WHERE a.account.id = :account_id")
    List<AccessToken> findByAccount(@Param("account_id") String account_id);

    @Query("SELECT a FROM AccessToken a WHERE a.token = :token")
    AccessToken find(@Param("token") String token);
}
