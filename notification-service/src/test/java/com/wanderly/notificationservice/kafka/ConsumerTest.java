package com.wanderly.notificationservice.kafka;

import com.wanderly.common.dto.VerificationEmailMessage;
import com.wanderly.notificationservice.service.MailService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.Mockito.*;

class ConsumerTest {

    private MailService mailService;
    private VerificationEmailConsumer consumer;

    @BeforeEach
    void setUp() {
        mailService = mock(MailService.class);
        consumer = new VerificationEmailConsumer(mailService);
    }

    @Test
    void listen_processesMessageAndAcknowledges() {
        String email = "test@example.com";
        String code = "123456";
        VerificationEmailMessage message = new VerificationEmailMessage(email, code);

        ConsumerRecord<String, VerificationEmailMessage> consumerRecord = mock(ConsumerRecord.class);
        Acknowledgment acknowledgment = mock(Acknowledgment.class);

        when(consumerRecord.value()).thenReturn(message);

        consumer.listen(consumerRecord, acknowledgment);

        verify(mailService).sendVerificationCodeEmail(email, code);
        verify(acknowledgment).acknowledge();
    }
}
