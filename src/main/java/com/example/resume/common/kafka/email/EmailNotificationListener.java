package com.example.resume.common.kafka.email;

import com.example.resume.cv.dto.EmailNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class EmailNotificationListener {
    private final EmailNotificationProducer producer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(EmailNotificationEvent event) {
        producer.send(event.getToEmail(), event.getResumeTitle());
    }
}
