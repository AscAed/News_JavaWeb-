package com.zhouyi.service;

/**
 * Service interface for sending emails via SMTP
 */
public interface EmailService {

    /**
     * 发送验证码邮件
     * 
     * @param toEmail 目标邮箱
     * @param code    验证码
     * @return 是否发送成功
     */
    boolean sendVerificationEmail(String toEmail, String code);
}
