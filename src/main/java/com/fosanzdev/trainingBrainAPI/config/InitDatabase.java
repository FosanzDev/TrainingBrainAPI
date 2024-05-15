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
                moodRepository.save(new Mood("Happy", "You feel good and happy", "YELLOW", "/static/img/moods/happy.png"));
                moodRepository.save(new Mood("Sad", "You feel sad or slightly depressed", "BLUE", "/static/img/moods/sad.png"));
                moodRepository.save(new Mood("Angry", "You feel angry or irritated", "RED", "/static/img/moods/angry.png"));
                moodRepository.save(new Mood("Anxious", "You feel anxious, nervous or worried", "ORANGE", "/static/img/moods/anxious.png"));
                moodRepository.save(new Mood("Relaxed", "You feel relaxed, calm or peaceful", "GREEN", "/static/img/moods/relaxed.png"));
                moodRepository.save(new Mood("Stressed", "You feel stressed, like you have too much to do", "RED", "/static/img/moods/stressed.png"));
                moodRepository.save(new Mood("Tired", "You feel tired, sleepy or exhausted", "GRAY", "/static/img/moods/tired.png"));
                moodRepository.save(new Mood("Energetic", "You feel energetic, awake or lively", "YELLOW", "/static/img/moods/energetic.png"));
                moodRepository.save(new Mood("Sick", "You feel sick, physically unwell or nauseous", "GRAY", "/static/img/moods/sick.png"));
                moodRepository.save(new Mood("Healthy", "You feel healthy, physically well or strong", "GREEN", "/static/img/moods/healthy.png"));
                moodRepository.save(new Mood("Demotivated", "You feel demotivated, uninspired or unenthusiastic", "BLUE", "/static/img/moods/demotivated.png"));
                moodRepository.save(new Mood("Motivated", "You feel motivated, inspired or enthusiastic", "YELLOW", "/static/img/moods/motivated.png"));
                moodRepository.save(new Mood("Bored", "You feel bored, uninterested or unchallenged", "GRAY", "/static/img/moods/bored.png"));
                moodRepository.save(new Mood("Excited", "You feel excited, eager or enthusiastic", "YELLOW", "/static/img/moods/excited.png"));
                moodRepository.save(new Mood("Scared", "You feel scared, frightened or terrified", "BLACK", "/static/img/moods/scared.png"));
                moodRepository.save(new Mood("Confused", "You feel confused, puzzled or disoriented", "GRAY", "/static/img/moods/confused.png"));
                moodRepository.save(new Mood("Frustrated", "You feel frustrated, annoyed or irritated", "RED", "/static/img/moods/frustrated.png"));
                moodRepository.save(new Mood("Satisfied", "You feel satisfied, content or pleased", "GREEN", "/static/img/moods/satisfied.png"));
                moodRepository.save(new Mood("Insecure", "You feel insecure, uncertain or unsafe", "GRAY", "/static/img/moods/insecure.png"));
                moodRepository.save(new Mood("Secure", "You feel secure, certain or safe", "GREEN", "/static/img/moods/secure.png"));
                moodRepository.save(new Mood("Grateful", "You feel grateful, thankful or appreciative", "GREEN", "/static/img/moods/grateful.png"));
                moodRepository.save(new Mood("Hopeful", "You feel hopeful, optimistic or positive", "GREEN", "/static/img/moods/hopeful.png"));
                moodRepository.save(new Mood("Desperate", "You feel desperate, hopeless or negative", "BLACK", "/static/img/moods/desperate.png"));
                moodRepository.save(new Mood("Ashamed", "You feel ashamed, guilty or embarrassed", "GRAY", "/static/img/moods/ashamed.png"));
                moodRepository.save(new Mood("Proud", "You feel proud, accomplished or successful", "GREEN", "/static/img/moods/proud.png"));
            }
        };
    }
}
