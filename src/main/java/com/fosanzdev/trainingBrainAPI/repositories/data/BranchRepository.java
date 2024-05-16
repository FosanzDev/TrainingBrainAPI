package com.fosanzdev.trainingBrainAPI.repositories.data;

import com.fosanzdev.trainingBrainAPI.models.details.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {
}
