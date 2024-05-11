package com.fosanzdev.trainingBrainAPI.repositories.auth;

import com.fosanzdev.trainingBrainAPI.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    @Query("SELECT r FROM RefreshToken r WHERE r.account.id = :account_id")
    List<RefreshToken> findByAccount(@Param("account_id") String account_id);

    @Query("SELECT r FROM RefreshToken r WHERE r.token = :token")
    RefreshToken find(@Param("token") String token);
}
