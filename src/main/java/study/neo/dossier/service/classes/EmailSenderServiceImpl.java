package study.neo.dossier.service.classes;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import study.neo.dossier.dto.EmailMessage;
import study.neo.dossier.service.interfaces.EmailSenderService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderServiceImpl implements EmailSenderService {
    private final JavaMailSender javaMailSender;
    @Value("${finish.registration.link}")
    private String finishRegistrationLink;
    @Value("${create.documents.link}")
    private String createDocumentsLink;
    @Value("${send.documents.link}")
    private String sendDocumentsLink;
    @Value("${send.ses.link}")
    private String sendSesLink;

    @Override
    public void getThemeOfMessage(EmailMessage emailMessage) throws MessagingException {
        log.info("Получаем тему сообщения");
        switch (emailMessage.getTheme()) {
            case FINISH_REGISTRATION -> {
                log.info("Сообщение имеет тему FINISH_REGISTRATION");
                createEmail(emailMessage, setFinishRegistrationMessage(emailMessage), false);
            }
            case CREATE_DOCUMENTS -> {
                log.info("Сообщение имеет тему CREATE_DOCUMENTS");
                createEmail(emailMessage, setCreateDocumentsMessage(emailMessage), false);
            }
            case SEND_DOCUMENTS -> {
                log.info("Сообщение имеет тему SEND_DOCUMENTS");
                createEmail(emailMessage, setSendDocumentsMessage(emailMessage), true);
            }
            case SEND_SES -> {
                log.info("Сообщение имеет тему SEND_SES");
                createEmail(emailMessage, setSendSesMessage(emailMessage), false);
            }
            case CREDIT_ISSUED -> {
                log.info("Сообщение имеет тему CREDIT_ISSUED");
                createEmail(emailMessage, setCreditIssuedMessage(), false);
            }
            case APPLICATION_DENIED -> {
                log.info("Сообщение имеет тему APPLICATION_DENIED");
                createEmail(emailMessage, setApplicationDeniedMessage(), false);
            }
        }
    }

    private void sendEmail(String from,
                           String to,
                           String subject,
                           String message,
                           Boolean hasAttachment,
                           Long applicationId)
            throws MessagingException {
        log.info("Составляем письмо в EmailSenderServiceImpl для отправления на почту клиента: from: {}, " +
                "to: {}, " +
                "subject: {}, " +
                "message: {}, " +
                "hasAttachment: {}", from, to, subject, message, hasAttachment);
        FileSystemResource fileSystemResource = createAttachmentDocument(applicationId);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        mimeMessage.setHeader("From", from);
        mimeMessage.setHeader("To", to);
        mimeMessage.setHeader("Subject", subject);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setText(message);
        helper.setSubject(subject);
        if (hasAttachment) {
            log.info("Добавляем вложение к электронному письму");
            helper.addAttachment(applicationId + ".txt", fileSystemResource);
        }
        log.info("Отправляем письмо: {} клиенту", mimeMessage);
        javaMailSender.send(mimeMessage);
    }

    private FileSystemResource createAttachmentDocument(Long applicationId) {
        log.info("Создаем вложенный документ");
        String pathToDir = "src/main/resources/static";
        File dir = new File(pathToDir);
        dir.mkdir();
        String pathToAttachment = pathToDir + "/" + applicationId + ".txt";
        File file = new File(pathToAttachment);
        FileSystemResource fileSystemResource = new FileSystemResource(file);
        try (FileWriter fr = new FileWriter(file);
             BufferedWriter br = new BufferedWriter(fr)) {
            log.info("Заполняем вложенный документ");
            br.write("Иммитирую вложенный документ для заявки с applicationId: " + applicationId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileSystemResource;
    }

    private void createEmail(EmailMessage emailMessage, String message, Boolean hasAttachment)
            throws MessagingException {
        log.info("Наполняем электронное письмо данными: message: {}," +
                        " address: {}," +
                        " theme: {}," +
                        " hasAttachment: {}", message,
                emailMessage.getAddress(),
                emailMessage.getTheme(),
                hasAttachment);
        sendEmail("fancestheart@gmail.com",
                emailMessage.getAddress(),
                emailMessage.getTheme().name(),
                message,
                hasAttachment,
                emailMessage.getApplicationId());
    }

    private String setFinishRegistrationMessage(EmailMessage emailMessage) {
        return "Здравствуйте! Необходимо завершить регистрацию для заявки с id: "
                + emailMessage.getApplicationId() + ". Для завершения регистрации пройдите по ссылке: " +
                finishRegistrationLink + emailMessage.getApplicationId();
    }

    private String setCreateDocumentsMessage(EmailMessage emailMessage) {
        return "Здравствуйте! Скоринг данных успешно пройден. " +
                "Отправьте запрос на создание документов по ссылке: " +
                createDocumentsLink + emailMessage.getApplicationId() + "/send";
    }

    private String setSendDocumentsMessage(EmailMessage emailMessage) {
        return "Здравствуйте! Ваши документы успешно сформированы. " +
                "Предлагаем отправить запрос на подписание документов по ссылке: " +
                sendDocumentsLink + emailMessage.getApplicationId() + "/sign";
    }

    private String setSendSesMessage(EmailMessage emailMessage) {
        return "Здравствуйте! Ваш код ПЭП (SES): " + emailMessage.getSesCode() + " . " +
                "Предлагаем отправить данный код вместе с идентификатором вашей заявки, а именно:" +
                emailMessage.getApplicationId() + " по ссылке: " + sendSesLink +
                emailMessage.getApplicationId() + "/code";
    }

    private String setCreditIssuedMessage() {
        return "Здравствуйте! Наши поздравления - вы успешно взяли кредит!";
    }

    private String setApplicationDeniedMessage() {
        return "Заявка отклонена";
    }
}
