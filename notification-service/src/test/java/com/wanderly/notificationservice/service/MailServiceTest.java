package com.wanderly.notificationservice.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

class MailServiceTest {

    private JavaMailSender javaMailSender;
    private MailProperties mailProperties;
    private MailService mailService;

    @BeforeEach
    void setUp() {
        javaMailSender = mock(JavaMailSender.class);
        mailProperties = new MailProperties();
        mailProperties.setUsername("noreply@wanderly.com");
        mailService = new MailService(javaMailSender, mailProperties);
    }

    @Test
    void sendVerificationCodeEmail_sendsMailWithCorrectContent() {
        String recipient = "user@example.com";
        String code = "123456";
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        mailService.sendVerificationCodeEmail(recipient, code);

        verify(javaMailSender).send(mimeMessage);

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(javaMailSender).send(captor.capture());
    }
}
