package com.fosanzdev.trainingBrainAPI.config;

import com.fosanzdev.trainingBrainAPI.models.mood.Mood;
import com.fosanzdev.trainingBrainAPI.repositories.mood.MoodRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitDatabase {

    @Bean
    CommandLineRunner initMoods(MoodRepository moodRepository) {
        return args -> {
            if (moodRepository.count() == 0){
                moodRepository.save(new Mood("Happy", "You feel good and happy", "YELLOW"));
                moodRepository.save(new Mood("Sad", "You feel sad or slightly depressed", "BLUE"));
                moodRepository.save(new Mood("Angry", "You feel angry or irritated", "RED"));
                moodRepository.save(new Mood("Anxious", "You feel anxious, nervous or worried", "ORANGE"));
                moodRepository.save(new Mood("Relaxed", "You feel relaxed, calm or peaceful", "GREEN"));
                moodRepository.save(new Mood("Stressed", "You feel stressed, like you have too much to do", "RED"));
                moodRepository.save(new Mood("Tired", "You feel tired, sleepy or exhausted", "GRAY"));
                moodRepository.save(new Mood("Energetic", "You feel energetic, awake or lively", "YELLOW"));
                moodRepository.save(new Mood("Sick", "You feel sick, physically unwell or nauseous", "GRAY"));
                moodRepository.save(new Mood("Healthy", "You feel healthy, physically well or strong", "GREEN"));
                moodRepository.save(new Mood("Demotivated", "You feel demotivated, uninspired or unenthusiastic", "BLUE"));
                moodRepository.save(new Mood("Motivated", "You feel motivated, inspired or enthusiastic", "YELLOW"));
                moodRepository.save(new Mood("Bored", "You feel bored, uninterested or unchallenged", "GRAY"));
                moodRepository.save(new Mood("Excited", "You feel excited, eager or enthusiastic", "YELLOW"));
                moodRepository.save(new Mood("Scared", "You feel scared, frightened or terrified", "BLACK"));
                moodRepository.save(new Mood("Confused", "You feel confused, puzzled or disoriented", "GRAY"));
                moodRepository.save(new Mood("Frustrated", "You feel frustrated, annoyed or irritated", "RED"));
                moodRepository.save(new Mood("Satisfied", "You feel satisfied, content or pleased", "GREEN"));
                moodRepository.save(new Mood("Insecure", "You feel insecure, uncertain or unsafe", "GRAY"));
                moodRepository.save(new Mood("Secure", "You feel secure, certain or safe", "GREEN"));
                moodRepository.save(new Mood("Grateful", "You feel grateful, thankful or appreciative", "GREEN"));
                moodRepository.save(new Mood("Hopeful", "You feel hopeful, optimistic or positive", "GREEN"));
                moodRepository.save(new Mood("Desperate", "You feel desperate, hopeless or negative", "BLACK"));
                moodRepository.save(new Mood("Ashamed", "You feel ashamed, guilty or embarrassed", "GRAY"));
                moodRepository.save(new Mood("Proud", "You feel proud, accomplished or successful", "GREEN"));
            }
        };
    }

}
