package com.fosanzdev.trainingBrainAPI.repositories.data;

import com.fosanzdev.trainingBrainAPI.models.details.WorkTitle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkTitleRepository extends JpaRepository<WorkTitle, String> {
}
