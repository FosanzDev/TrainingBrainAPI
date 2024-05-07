package com.fosanzdev.trainingBrainAPI.repositories.auth;

import com.fosanzdev.trainingBrainAPI.models.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AccessTokenRepository extends JpaRepository<AccessToken, UUID> {
}
