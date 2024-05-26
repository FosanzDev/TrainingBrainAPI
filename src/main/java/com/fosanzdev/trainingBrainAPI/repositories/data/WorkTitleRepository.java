package com.fosanzdev.trainingBrainAPI.repositories.data;

import com.fosanzdev.trainingBrainAPI.models.data.WorkTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WorkTitleRepository extends JpaRepository<WorkTitle, Long> {

    @Query("SELECT w from WorkTitle w where w.branch.id = ?1")
    List<WorkTitle> findByBranch(Long branchId);
}
