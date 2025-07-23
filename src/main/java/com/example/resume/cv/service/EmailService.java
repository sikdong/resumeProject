package com.example.resume.cv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendReviewNotification(String toEmail, String resumeTitle) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("이력서에 새로운 평가가 등록되었습니다");
        message.setText("당신의 이력서 '" + resumeTitle + "'에 새로운 평가가 등록되었습니다.");

        mailSender.send(message);
    }
}

