package com.fosanzdev.trainingBrainAPI.config;

import com.fosanzdev.trainingBrainAPI.models.details.Branch;
import com.fosanzdev.trainingBrainAPI.models.details.Skill;
import com.fosanzdev.trainingBrainAPI.models.details.WorkTitle;
import com.fosanzdev.trainingBrainAPI.models.mood.Mood;
import com.fosanzdev.trainingBrainAPI.repositories.data.BranchRepository;
import com.fosanzdev.trainingBrainAPI.repositories.data.SkillRepository;
import com.fosanzdev.trainingBrainAPI.repositories.data.WorkTitleRepository;
import com.fosanzdev.trainingBrainAPI.repositories.mood.MoodRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitDatabase {
    /*
     * This class is used to initialize the database with default data
     */

    @Bean
    CommandLineRunner initMoods(MoodRepository moodRepository) {
        return args -> {
            if (moodRepository.count() == 0) {
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

    @Bean
    CommandLineRunner initBranches(BranchRepository branchRepository) {
        return args -> {
            if (branchRepository.count() == 0) {
                branchRepository.save(new Branch(1L, "Psychology"));
                branchRepository.save(new Branch(2L, "Psychiatry"));
                branchRepository.save(new Branch(3L, "Clinical Psychology"));
                branchRepository.save(new Branch(4L, "Counseling Psychology"));
                branchRepository.save(new Branch(5L, "Child and Adolescent Psychiatry"));
                branchRepository.save(new Branch(6L, "Forensic Psychiatry"));
                branchRepository.save(new Branch(7L, "Geriatric Psychiatry"));
                branchRepository.save(new Branch(8L, "Addiction Psychiatry"));
                branchRepository.save(new Branch(9L, "Neuropsychiatry"));
                branchRepository.save(new Branch(10L, "Social Work"));
                branchRepository.save(new Branch(11L, "Mental Health Counseling"));
                branchRepository.save(new Branch(12L, "Psychotherapy"));
                branchRepository.save(new Branch(13L, "Occupational Therapy"));
            }
        };
    }

    @Transactional
    @Bean
    CommandLineRunner initWorkTitles(WorkTitleRepository workTitleRepository, BranchRepository branchRepository) {
        return args -> {
            if (workTitleRepository.count() == 0) {
                // Psychology
                workTitleRepository.save(new WorkTitle("Psychologist", branchRepository.findById(1L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Auxiliary Psychologist", branchRepository.findById(1L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Educational Psychologist", branchRepository.findById(1L).orElse(null)));

                // Psychiatry
                workTitleRepository.save(new WorkTitle("Psychiatrist", branchRepository.findById(2L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Consultant Psychiatrist", branchRepository.findById(2L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Resident Psychiatrist", branchRepository.findById(2L).orElse(null)));

                // Clinical Psychology
                workTitleRepository.save(new WorkTitle("Clinical Psychologist", branchRepository.findById(3L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Clinical Psychology Intern", branchRepository.findById(3L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Clinical Neuropsychologist", branchRepository.findById(3L).orElse(null)));

                // Counseling Psychology
                workTitleRepository.save(new WorkTitle("Counseling Psychologist", branchRepository.findById(4L).orElse(null)));
                workTitleRepository.save(new WorkTitle("School Counselor", branchRepository.findById(4L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Marriage and Family Therapist", branchRepository.findById(4L).orElse(null)));

                // Child and Adolescent Psychiatry
                workTitleRepository.save(new WorkTitle("Child Psychiatrist", branchRepository.findById(5L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Adolescent Psychiatrist", branchRepository.findById(5L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Pediatric Psychiatrist", branchRepository.findById(5L).orElse(null)));

                // Forensic Psychiatry
                workTitleRepository.save(new WorkTitle("Forensic Psychiatrist", branchRepository.findById(6L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Forensic Psychology Consultant", branchRepository.findById(6L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Court Liaison Psychiatrist", branchRepository.findById(6L).orElse(null)));

                // Geriatric Psychiatry
                workTitleRepository.save(new WorkTitle("Geriatric Psychiatrist", branchRepository.findById(7L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Geriatric Psychologist", branchRepository.findById(7L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Senior Care Consultant", branchRepository.findById(7L).orElse(null)));

                // Addiction Psychiatry
                workTitleRepository.save(new WorkTitle("Addiction Psychiatrist", branchRepository.findById(8L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Substance Abuse Counselor", branchRepository.findById(8L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Addiction Treatment Specialist", branchRepository.findById(8L).orElse(null)));

                // Neuropsychiatry
                workTitleRepository.save(new WorkTitle("Neuropsychiatrist", branchRepository.findById(9L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Behavioral Neurologist", branchRepository.findById(9L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Neuropsychologist", branchRepository.findById(9L).orElse(null)));

                // Social Work
                workTitleRepository.save(new WorkTitle("Social Worker", branchRepository.findById(10L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Clinical Social Worker", branchRepository.findById(10L).orElse(null)));
                workTitleRepository.save(new WorkTitle("School Social Worker", branchRepository.findById(10L).orElse(null)));

                // Mental Health Counseling
                workTitleRepository.save(new WorkTitle("Mental Health Counselor", branchRepository.findById(11L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Mental Health Therapist", branchRepository.findById(11L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Licensed Mental Health Counselor (LMHC)", branchRepository.findById(11L).orElse(null)));

                // Psychotherapy
                workTitleRepository.save(new WorkTitle("Psychotherapist", branchRepository.findById(12L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Family Psychotherapist", branchRepository.findById(12L).orElse(null)));
                workTitleRepository.save(new WorkTitle("Group Psychotherapist", branchRepository.findById(12L).orElse(null)));

                // Occupational Therapy
                workTitleRepository.save(new WorkTitle(37L, "Occupational Therapist", branchRepository.findById(13L).orElse(null)));
                workTitleRepository.save(new WorkTitle(38L, "Pediatric Occupational Therapist", branchRepository.findById(13L).orElse(null)));
                workTitleRepository.save(new WorkTitle(39L, "Geriatric Occupational Therapist", branchRepository.findById(13L).orElse(null)));
            }
        };
    }


    @Bean
    CommandLineRunner initSkills(SkillRepository skillRepository) {
        return args -> {
            if (skillRepository.count() == 0) {
                skillRepository.save(new Skill("Listening", "The ability to listen actively and attentively to others"));
                skillRepository.save(new Skill("Empathy", "The ability to understand and share the feelings of others"));
                skillRepository.save(new Skill("Communication", "The ability to communicate effectively with others"));
                skillRepository.save(new Skill("Critical Thinking", "The ability to analyze and evaluate information to make well-informed decisions"));
                skillRepository.save(new Skill("Problem-Solving", "The ability to find effective solutions to issues faced by clients"));
                skillRepository.save(new Skill("Patience", "The ability to remain calm and composed in challenging situations"));
                skillRepository.save(new Skill("Compassion", "The ability to show kindness and concern for others"));
                skillRepository.save(new Skill("Cultural Competence", "The ability to understand and respect cultural differences in clients"));
                skillRepository.save(new Skill("Adaptability", "The ability to adjust to new conditions and client needs"));
                skillRepository.save(new Skill("Ethics", "The adherence to ethical standards and principles in professional practice"));
                skillRepository.save(new Skill("Resilience", "The ability to recover quickly from difficulties and maintain mental well-being"));
                skillRepository.save(new Skill("Stress Management", "The ability to manage and reduce stress effectively"));
                skillRepository.save(new Skill("Conflict Resolution", "The ability to resolve disputes and conflicts in a constructive manner"));
                skillRepository.save(new Skill("Teamwork", "The ability to work effectively as part of a team"));
                skillRepository.save(new Skill("Time Management", "The ability to manage time efficiently and prioritize tasks"));
                skillRepository.save(new Skill("Client Assessment", "The ability to assess clients' needs, strengths, and areas for improvement"));
                skillRepository.save(new Skill("Therapeutic Techniques", "The knowledge and application of various therapeutic techniques and interventions"));
                skillRepository.save(new Skill("Confidentiality", "The ability to maintain client confidentiality and privacy"));
                skillRepository.save(new Skill("Documentation", "The ability to accurately document client interactions and progress"));
                skillRepository.save(new Skill("Motivational Interviewing", "The ability to encourage and motivate clients towards positive change"));
                skillRepository.save(new Skill("Boundary Setting", "The ability to establish and maintain professional boundaries with clients"));
                skillRepository.save(new Skill("Crisis Intervention", "The ability to intervene and provide support in crisis situations"));
                skillRepository.save(new Skill("Report Writing", "The ability to write clear and concise reports on client progress and treatment"));
                skillRepository.save(new Skill("Interpersonal Skills", "The ability to interact effectively and build relationships with clients"));
            }
        };
    }

}
