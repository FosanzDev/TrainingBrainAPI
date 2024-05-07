package com.fosanzdev.trainingBrainAPI.repositories.auth;

import com.fosanzdev.trainingBrainAPI.models.AuthCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AuthCodeRepository extends JpaRepository<AuthCode, UUID> {
}
