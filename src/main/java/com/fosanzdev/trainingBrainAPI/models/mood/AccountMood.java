package com.fosanzdev.trainingBrainAPI.models.mood;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "account_moods")
public class AccountMood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_account", referencedColumnName = "id")
    private Account account;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_mood", referencedColumnName = "id")
    private Mood mood;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @PrePersist
    public void prePersist() {
        date = new Date();
    }
}
