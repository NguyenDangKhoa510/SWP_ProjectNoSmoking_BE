package org.datcheems.swp_projectnosmoking.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
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
        String subject = "Yêu cầu đặt lại mật khẩu";

        String body = "<p>Xin chào,</p>"
                + "<p>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản của mình.</p>"
                + "<p>Mã xác thực của bạn là: <strong>" + code + "</strong></p>"
                + "<p>Vui lòng nhập mã này vào trang đặt lại mật khẩu để tiếp tục.</p>"
                + "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>"
                + "<p>Mã sẽ hết hạn sau 30 phút.</p>"
                + "<p>Trân trọng,</p>"
                + "<p>Đội ngũ No Smoking</p>";

        sendEmail(to, subject, body);
    }


    public void sendCoachCredentials(String toEmail, String username, String rawPassword) {
        String subject = "Tài khoản Coach đã được tạo thành công";
        String content = String.format("""
            Xin chào Coach,
            Tài khoản của bạn đã được tạo trên hệ thống No Smoking.

            Tài khoản đăng nhập (username): %s
            Tài khoản được tạo dựa trên email: %s
            Mật khẩu tạm thời: %s
            Vui lòng đăng nhập tại: http://localhost:8080/login
            Bấm đổi mật khẩu ngay sau khi đăng nhập.

            Trân trọng,
            No Smoking Team
            """, username, toEmail, rawPassword);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }

}
