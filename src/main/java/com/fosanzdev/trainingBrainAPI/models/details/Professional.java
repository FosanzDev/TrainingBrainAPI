package com.fosanzdev.trainingBrainAPI.models.details;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_account", referencedColumnName = "id")
    private Account account;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_work_title", referencedColumnName = "id")
    private WorkTitle workTitle;

    @OneToOne(mappedBy = "professional")
    private WorkDetails workDetails;

    @OneToMany(mappedBy = "professional", cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private List<ProfessionalSkill> professionalSkills;
}
