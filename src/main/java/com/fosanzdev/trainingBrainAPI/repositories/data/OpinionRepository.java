package com.fosanzdev.trainingBrainAPI.repositories.data;

import com.fosanzdev.trainingBrainAPI.models.data.Opinion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OpinionRepository extends JpaRepository<Opinion, String> {

    @Query("SELECT o FROM Opinion o WHERE o.user.id = ?1 AND o.professional.id = ?2")
    Opinion findOpinionByUserAndProfessional(String userId, String professionalId);

    @Query("SELECT o FROM Opinion o WHERE o.user.id = ?1")
    List<Opinion> getByUserId(String id);

    @Query("SELECT o FROM Opinion o WHERE o.professional.id = ?1")
    List<Opinion> getByProfessionalId(String id);
}
