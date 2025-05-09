package com.wanderly.notificationservice.kafka;

import com.wanderly.common.dto.VerificationEmailMessage;
import com.wanderly.notificationservice.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationEmailConsumer {

    private final MailService mailService;

    @KafkaListener(topics = "verification-email-topic", groupId = "notification-service-group")
    public void listen(ConsumerRecord<String, VerificationEmailMessage> record, Acknowledgment ack) {
        log.info("Received: {}", record.value());
        ack.acknowledge();

        mailService.sendVerificationCodeEmail(record.value().email(), record.value().verificationCode());
    }

}
