package com.zhouyi.service.impl;

import com.zhouyi.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${custom.mail.sender-email}")
    private String senderEmail;

    @Value("${custom.mail.sender-name}")
    private String senderName;

    @Override
    public boolean sendVerificationEmail(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(String.format("%s <%s>", senderName, senderEmail));
            helper.setTo(toEmail);
            helper.setSubject("【易闻趣事】账号注册验证码");

            String htmlContent = String.format(
                    "<h3>欢迎注册【易闻趣事】系统！</h3>" +
                    "<p>您的邮箱验证码是：<strong style='font-size:24px;color:#007bff;'>%s</strong></p>" +
                    "<p>请在5分钟内完成注册。如果不是您本人的操作，请忽略此邮件。</p>",
                    code
            );
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Successfully sent verification email to: {}", toEmail);
            return true;
        } catch (MessagingException e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error while sending email to: {}", toEmail, e);
            return false;
        }
    }
}
