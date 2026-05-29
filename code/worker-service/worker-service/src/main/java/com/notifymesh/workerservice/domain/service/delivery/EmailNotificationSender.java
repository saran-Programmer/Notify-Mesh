package com.notifymesh.workerservice.domain.service.delivery;

import com.notifymesh.workerservice.domain.model.FileAttachment;
import com.notifymesh.workerservice.domain.valueobject.Channel;
import com.notifymesh.workerservice.dto.NotificationEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationSender implements NotificationSender {

    private final JavaMailSender mailSender;

    private final String UTF8 = "UTF-8";

    @Override
    public Channel channel() {
        return Channel.EMAIL;
    }

    @Override
    public void send(NotificationEvent event, List<FileAttachment> attachments) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();

        boolean multipart = !attachments.isEmpty();
        
        MimeMessageHelper helper = new MimeMessageHelper(message, multipart, UTF8);

        helper.setTo(event.getRecipient());
        helper.setSubject(event.getSubject());
        helper.setText(event.getContent(), false);

        for (FileAttachment attachment : attachments) {

            helper.addAttachment(attachment.getFileName(), 
            new ByteArrayResource(attachment.getFileBytes()));
        }

        mailSender.send(message);
    }
}
