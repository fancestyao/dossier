package study.neo.dossier.service.interfaces;

import jakarta.mail.MessagingException;
import study.neo.dossier.dto.EmailMessage;

import java.io.IOException;

public interface EmailSenderService {
    void getThemeOfMessage(EmailMessage emailMessage)
            throws MessagingException, IOException;
}
