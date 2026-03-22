package com.campusapp.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String toEmail, String otp) {
        String subject = "CEvent - Password Reset OTP";
        String body = buildOtpEmailBody(otp);
        sendEmail(toEmail, subject, body);
    }

    private void sendEmail(String toEmail, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("ceventsappcom@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email to " + toEmail, e);
        }
    }

    private String buildOtpEmailBody(String otp) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto;">
                    <h2 style="color: #2c3e50;">CEvent - Password Reset</h2>
                    <p>You requested a password reset. Use the OTP below:</p>
                    <div style="background-color: #f4f4f4; padding: 20px; text-align: center;
                                border-radius: 8px; margin: 20px 0;">
                        <h1 style="color: #e74c3c; letter-spacing: 8px;">%s</h1>
                    </div>
                    <p>This OTP is valid for <strong>10 minutes</strong>.</p>
                    <p>If you did not request this, please ignore this email.</p>
                    <p style="color: #999; font-size: 12px;">— CEvent Team</p>
                </div>
                """.formatted(otp);
    }
}