package com.fosanzdev.trainingBrainAPI.repositories.mood;

import com.fosanzdev.trainingBrainAPI.models.mood.Mood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoodRepository extends JpaRepository<Mood, String> {
}
