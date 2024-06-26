package com.fosanzdev.trainingBrainAPI.models.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 500)
    private String publicBio;

    @Column(length = 500)
    private String privateBio;

    @Column(length = 5000)
    private String history;

    @Getter
    private String dateOfBirth;

    @JsonIgnore
    @OneToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_account", referencedColumnName = "id")
    private Account account;

    public void setDateOfBirth(String dateOfBirth) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    Date date = sdf.parse(dateOfBirth);

    // Check if the user is at least 13 years old
    Calendar thirteenYearsAgo = Calendar.getInstance();
    thirteenYearsAgo.add(Calendar.YEAR, -13);

    if (date.after(thirteenYearsAgo.getTime()))
        throw new ParseException("User must be at least 13 years old", 0);

    this.dateOfBirth = sdf.format(date);
}

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();

        map.put("id", id);
        map.put("name", account.getName());
        map.put("publicBio", publicBio);
        map.put("privateBio", privateBio);
        map.put("history", history);
        map.put("dateOfBirth", dateOfBirth);

        return map;
    }

    public Map<String, String> toPublicMap() {
        Map<String, String> map = new HashMap<>();

        map.put("id", id);
        map.put("name", account.getName());
        map.put("publicBio", publicBio);
        map.put("dateOfBirth", dateOfBirth);

        return map;
    }
}
