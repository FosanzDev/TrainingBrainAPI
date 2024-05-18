package com.fosanzdev.trainingBrainAPI.models.details;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "professionals")
public class Professional {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String public_bio;

    @JsonIgnore
    @OneToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_account", referencedColumnName = "id")
    private Account account;

    @OneToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_work_title", referencedColumnName = "id")
    private WorkTitle workTitle;

    @OneToMany(mappedBy = "professional", cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private List<WorkDetail> workDetails;

    @OneToMany(mappedBy = "professional", cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private List<ProfessionalSkill> professionalSkills;

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();

        map.put("public_bio", public_bio);

        if (workTitle == null)
            map.put("work_title", null);
        else
            map.put("work_title", workTitle.toMap());

        ArrayList<Object> workDetailsList = new ArrayList<>();
        for (WorkDetail workDetail : workDetails)
            workDetailsList.add(workDetail.toMap());
        map.put("workDetails", workDetailsList);

        ArrayList<Object> professionalSkillsList = new ArrayList<>();
        for (ProfessionalSkill professionalSkill : professionalSkills)
            professionalSkillsList.add(professionalSkill.toMap());
        map.put("professionalSkills", professionalSkillsList);

        return map;
    }
}
