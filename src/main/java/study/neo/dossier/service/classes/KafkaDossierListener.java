package study.neo.dossier.service.classes;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import study.neo.dossier.dto.EmailMessage;
import study.neo.dossier.service.interfaces.EmailSenderService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaDossierListener {
    private final EmailSenderService emailSenderService;

    @KafkaListener(topics = {"finish-registration",
                             "create-documents",
                             "send-documents",
                             "credit-issued",
                             "send-ses",
                             "application-denied"},
                   groupId = "studyNeo")
    void listener(EmailMessage emailMessage) throws MessagingException, IOException {
        log.info("Сообщение доставлено в метод listener consumer'a Кафки");
        emailSenderService.getThemeOfMessage(emailMessage);
    }
}
