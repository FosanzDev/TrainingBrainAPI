package com.fosanzdev.trainingBrainAPI.repositories.data;

import com.fosanzdev.trainingBrainAPI.models.data.WorkDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkDetailRepository extends JpaRepository<WorkDetail, Long> {
}
