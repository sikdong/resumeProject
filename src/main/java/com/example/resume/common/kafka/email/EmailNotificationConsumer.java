package com.example.resume.common.kafka.email;

import com.example.resume.cv.dto.EmailNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class EmailNotificationConsumer {

    private final JavaMailSender mailSender;

    @KafkaListener(topics = "email-notification", groupId = "debug-group")
    public void consume(EmailNotificationEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getToEmail());
        message.setSubject("이력서에 새로운 평가가 등록되었습니다");
        message.setText("당신의 이력서 '" + event.getResumeTitle() + "'에 새로운 평가가 등록되었습니다.");
        mailSender.send(message);
    }
}