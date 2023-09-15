package study.neo.dossier.service;

import jakarta.mail.MessagingException;

import java.io.IOException;

public interface EmailSenderService {
    void sendEmail(String from, String to, String subject, String message, Boolean hasAttachment)
            throws IOException, MessagingException;
}
