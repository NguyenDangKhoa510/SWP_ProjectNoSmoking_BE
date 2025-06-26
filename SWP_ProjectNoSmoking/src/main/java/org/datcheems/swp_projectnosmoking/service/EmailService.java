package org.datcheems.swp_projectnosmoking.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true indicates HTML content

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendPasswordResetEmail(String to, String code, String baseUrl) {
        String subject = "Password Reset Request";
        String body = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Your verification code is: <strong>" + code + "</strong></p>"
                + "<p>Please enter this code on the password reset page to continue.</p>"
                + "<p>If you did not request a password reset, please ignore this email.</p>"
                + "<p>The code will expire in 30 minutes.</p>"
                + "<p>Thank you,</p>"
                + "<p>The Quit Smoking Team</p>";

        sendEmail(to, subject, body);
    }
}
