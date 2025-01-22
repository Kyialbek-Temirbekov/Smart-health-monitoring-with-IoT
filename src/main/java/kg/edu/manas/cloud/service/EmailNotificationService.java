package kg.edu.manas.cloud.service;

import kg.edu.manas.cloud.model.data.record.EmailMessageRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {
    private final JavaMailSender mailSender;

    public void sendMessage(EmailMessageRecord message) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(message.recipient());
            simpleMailMessage.setSubject(message.subject());
            simpleMailMessage.setText(message.body());
            mailSender.send(simpleMailMessage);
        } catch (MailException e) {
            log.warn(e.getLocalizedMessage());
        }
    }
}
