package com.example.resume.common.kafka.email;

import com.example.resume.cv.dto.EmailNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificationProducer {
    private final KafkaTemplate<String, EmailNotificationEvent> kafkaTemplate;


    public void send(String toEmail, String resumeTitle) {
        kafkaTemplate.send("email-notification", new EmailNotificationEvent(toEmail, resumeTitle));
    }
}