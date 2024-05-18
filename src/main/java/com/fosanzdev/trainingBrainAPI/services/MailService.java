package com.fosanzdev.trainingBrainAPI.services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailService {

    private final String username;
    private final String password;

    public MailService(String username, String password){
        this.username = username;
        this.password = password;
    }

    public void sendMail(String to, String subject, String text){
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.host", "smtp.ionos.es");
        props.put("mail.smtp.port", "465");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        try{
            message.setFrom(new InternetAddress("noreply@trainingbrain.fosanz.dev"));
            message.setRecipient(Message.RecipientType.TO, InternetAddress.parse(to)[0]);
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}