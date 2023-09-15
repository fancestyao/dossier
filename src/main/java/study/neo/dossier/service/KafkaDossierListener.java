package study.neo.dossier.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import study.neo.dossier.dto.EmailMessage;

import java.io.IOException;

@Component
@RequiredArgsConstructor
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
        String message;
        switch (emailMessage.getTheme()) {
            case FINISH_REGISTRATION -> {
                message = "Здравствуйте! Необходимо завершить регистрацию для заявки с id: "
                        + emailMessage.getApplicationId() + ". Для завершения регистрации пройдите по ссылке: " +
                        "http://localhost:9090/deal/calculate/" + emailMessage.getApplicationId();
                createEmail(emailMessage, message, false);
            }
            case CREATE_DOCUMENTS -> {
                message = "Здравствуйте! Скоринг данных успешно пройден. " +
                        "Отправьте запрос на создание документов по ссылке: " +
                        "http://localhost:9090/deal/document/" + emailMessage.getApplicationId() + "/send";
                createEmail(emailMessage, message, false);
            }
            case SEND_DOCUMENTS -> {
                message = "Здравствуйте! Ваши документы успешно сформированы. " +
                        "Предлагаем отправить запрос на подписание документов по ссылке:" +
                        " http://localhost:9090/document/"
                        + emailMessage.getApplicationId() + "/sign";
                createEmail(emailMessage, message, true);
            }
            case SEND_SES -> {
                message = "Здравствуйте! Ваш код ПЭП (SES): " + emailMessage.getSesCode() + " . " +
                        "Предлагаем отправить данный код вместе с идентификатором вашей заявки, а именно:" +
                        emailMessage.getApplicationId() + " по ссылке: http://localhost:9090/document/" +
                        emailMessage.getApplicationId() + "/code";
                createEmail(emailMessage, message, false);
            }
            case CREDIT_ISSUED -> {
                message = "Здравствуйте! Наши поздравления - вы успешно взяли кредит!";
                createEmail(emailMessage, message, false);
            }
            case APPLICATION_DENIED -> {
                message = "Заявка отклонена";
                createEmail(emailMessage, message, false);
            }
        }
    }

    private void createEmail(EmailMessage emailMessage, String message, Boolean hasAttachment)
            throws MessagingException, IOException {
        emailSenderService.sendEmail("fancestheart@gmail.com",
                emailMessage.getAddress(),
                emailMessage.getTheme().name(),
                message,
                hasAttachment);
    }
}
