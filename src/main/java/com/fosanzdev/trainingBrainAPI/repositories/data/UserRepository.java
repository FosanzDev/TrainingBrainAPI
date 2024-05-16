package com.fosanzdev.trainingBrainAPI.repositories.data;

import com.fosanzdev.trainingBrainAPI.models.details.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String>{
}
