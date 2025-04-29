package com.wanderly.notificationservice.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;

    public void sendVerificationCodeEmail(String to, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailProperties.getUsername(), "Wanderly");
            helper.setTo(to);
            helper.setSubject("Welcome to Wanderly!");

            String htmlContent = """
                    <!DOCTYPE html>
                        <html>
                        <head>
                          <meta charset="UTF-8">
                          <meta name="viewport" content="width=device-width, initial-scale=1.0">
                          <title>Welcome to Wanderly</title>
                        </head>
                        <body style="margin: 0; padding: 0; background-color: #F4F4F4">
                          <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #F4F4F4; padding: 40px 0;">
                            <tr>
                              <td align="center">
                                <table width="650px" style="background-color: #E6EAED; padding: 30px; border-radius: 4px; box-shadow: 0 2px 4px -1px rgba(0, 0, 0, 0.07), 0 4px 5px 0 rgba(0, 0, 0, 0.05), 0 1px 10px 0 rgba(0, 0, 0, 0.05);">
                                  <tr>
                                    <td align="center" style="font-size: 24px; font-weight: 600; color: #353535;">
                                      Verify Your Email
                                    </td>
                                  </tr>
                                  <tr>
                                    <td align="center" style="font-size: 16px; color: #353535; padding-top: 10px;">
                                      Please use the code below to verify your email address.
                                    </td>
                                  </tr>
                                  <tr>
                                    <td align="center" style="padding: 20px 0;">
                                      <table width="50%%" style="background-color: #ffffff; padding: 15px 20px; border-radius: 8px;">
                                        <tr>
                                          <td align="center" style="font-size: 24px; font-weight: bold; color: #353535;">
                                            %s
                                          </td>
                                        </tr>
                                      </table>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td align="center" style="font-size: 12px; color: #777; padding-top: 10px;">
                                      This code will expire in 15 minutes.<br/>If you did not request this, you can safely ignore this email.
                                    </td>
                                  </tr>
                                  <tr>
                                    <td align="center" style="font-size: 12px; color: #777; padding-top: 20px;">
                                      Thank you,<br>The Wanderly Team
                                    </td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
                        </body>
                        </html>
                    """.formatted(code);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("Verification email sent to {}", to);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
