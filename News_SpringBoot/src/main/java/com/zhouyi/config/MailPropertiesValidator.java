package com.zhouyi.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MailPropertiesValidator implements InitializingBean {

    @Value("${spring.mail.username:}")
    private String username;

    @Value("${spring.mail.password:}")
    private String password;

    @Override
    public void afterPropertiesSet() {
        if (!StringUtils.hasText(username) || username.startsWith("${")) {
            throw new IllegalStateException("MAIL_USERNAME environment variable is missing. It is required for sending emails.");
        }
        if (!StringUtils.hasText(password) || password.startsWith("${")) {
            throw new IllegalStateException("MAIL_PASSWORD environment variable is missing. It is required for sending emails.");
        }
    }
}
