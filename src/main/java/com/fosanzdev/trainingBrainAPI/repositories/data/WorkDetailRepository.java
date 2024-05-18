package com.fosanzdev.trainingBrainAPI.repositories.data;

import com.fosanzdev.trainingBrainAPI.models.details.WorkDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkDetailRepository extends JpaRepository<WorkDetail, Long> {
}
