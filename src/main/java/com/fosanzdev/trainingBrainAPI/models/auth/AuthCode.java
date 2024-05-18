package com.fosanzdev.trainingBrainAPI.models.auth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Random;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "auth_codes")
public class AuthCode {

    @Id
    private String code;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "fk_account", referencedColumnName = "id")
    private Account account;

    @PrePersist
    public void generateCode(){
        this.code = generateRandomCode();
    }

    public String generateRandomCode(){
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        do{
            if (System.currentTimeMillis()*random.nextInt() % 2 == 0){
                if (random.nextBoolean()){
                    code.append((char) (random.nextInt(26) + 'A'));
                } else {
                    code.append((char) (random.nextInt(26) + 'a'));
                }
            } else {
                code.append(random.nextInt(10));
            }
        } while (code.length() < 6);

        return code.toString();
    }
}