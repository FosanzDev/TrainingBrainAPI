package com.fosanzdev.trainingBrainAPI.repositories.auth;

import com.fosanzdev.trainingBrainAPI.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
}
