package study.neo.dossier.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class EmailSenderServiceImpl implements EmailSenderService {
    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String from, String to, String subject, String message, Boolean hasAttachment)
            throws IOException, MessagingException {
        String pathToAttachment = "static/attachment.txt";
        String filePath = new ClassPathResource(pathToAttachment).getFile().getAbsolutePath();
        FileSystemResource fileSystemResource = new FileSystemResource(new File(filePath));
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
            helper.addAttachment("attachment.txt", fileSystemResource);
        }
        javaMailSender.send(mimeMessage);
    }
}
