package com.zhouyi.service.impl;

import com.zhouyi.common.result.Result;
import com.zhouyi.entity.User;
import com.zhouyi.entity.VerificationToken;
import com.zhouyi.mapper.UserMapper;
import com.zhouyi.mapper.VerificationTokenMapper;
import com.zhouyi.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Random;
import com.zhouyi.service.EmailService;

/**
 * Verification service implementation
 */
@Service
public class VerificationServiceImpl implements VerificationService {

    @Autowired
    private VerificationTokenMapper verificationTokenMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

    @Autowired
    private com.zhouyi.service.CaptchaService captchaService;

    private static final int TOKEN_EXPIRY_MINUTES = 15;
    private static final int REGISTER_TOKEN_EXPIRY_MINUTES = 5;
    private static final String TOKEN_TYPE_EMAIL = "EMAIL";
    private static final String TOKEN_TYPE_PHONE = "PHONE";
    private static final String TOKEN_TYPE_REGISTER = "REGISTER";

    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public Result<String> generateEmailVerificationToken(Integer userId, String newEmail) {
        User existingUser = userMapper.selectUserByPhone(newEmail);
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            return Result.error("Email already in use by another user");
        }

        String token = generateSixDigitToken();
        Date expiresAt = new Date(System.currentTimeMillis() + TOKEN_EXPIRY_MINUTES * 60 * 1000);

        VerificationToken existingToken = verificationTokenMapper.selectTokenByUserIdAndType(userId, TOKEN_TYPE_EMAIL);

        if (existingToken != null) {
            existingToken.setNewValue(newEmail);
            existingToken.setToken(token);
            existingToken.setExpiresAt(expiresAt);
            verificationTokenMapper.updateToken(existingToken);
        } else {
            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setUserId(userId);
            verificationToken.setTokenType(TOKEN_TYPE_EMAIL);
            verificationToken.setNewValue(newEmail);
            verificationToken.setToken(token);
            verificationToken.setExpiresAt(expiresAt);
            verificationTokenMapper.insertToken(verificationToken);
        }

        boolean success = emailService.sendVerificationEmail(newEmail, token);
        if (!success) {
            return Result.error("邮件发送失败，请稍后重试");
        }

        return Result.successWithMessageAndData(
                "Verification code sent. Code: " + token + " (expires in " + TOKEN_EXPIRY_MINUTES + " minutes)", token);
    }

    @Override
    @Transactional
    public Result<String> generatePhoneVerificationToken(Integer userId, String newPhone) {
        User existingUser = userMapper.selectUserByPhone(newPhone);
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            return Result.error("Phone number already in use by another user");
        }

        String token = generateSixDigitToken();
        Date expiresAt = new Date(System.currentTimeMillis() + TOKEN_EXPIRY_MINUTES * 60 * 1000);

        VerificationToken existingToken = verificationTokenMapper.selectTokenByUserIdAndType(userId, TOKEN_TYPE_PHONE);

        if (existingToken != null) {
            existingToken.setNewValue(newPhone);
            existingToken.setToken(token);
            existingToken.setExpiresAt(expiresAt);
            verificationTokenMapper.updateToken(existingToken);
        } else {
            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setUserId(userId);
            verificationToken.setTokenType(TOKEN_TYPE_PHONE);
            verificationToken.setNewValue(newPhone);
            verificationToken.setToken(token);
            verificationToken.setExpiresAt(expiresAt);
            verificationTokenMapper.insertToken(verificationToken);
        }

        return Result.successWithMessageAndData(
                "Verification code sent. Code: " + token + " (expires in " + TOKEN_EXPIRY_MINUTES + " minutes)", token);
    }

    @Override
    @Transactional
    public Result<String> verifyEmailToken(Integer userId, String token) {
        VerificationToken verificationToken = verificationTokenMapper.selectTokenByUserIdTypeAndToken(
                userId, TOKEN_TYPE_EMAIL, token);

        if (verificationToken == null) {
            return Result.error("Invalid verification code");
        }

        if (verificationToken.getExpiresAt().before(new Date())) {
            verificationTokenMapper.deleteTokenByUserIdAndType(userId, TOKEN_TYPE_EMAIL);
            return Result.error("Verification code has expired");
        }

        User existingUser = userMapper.selectUserByPhone(verificationToken.getNewValue());
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            return Result.error("Email already in use by another user");
        }

        User user = userMapper.selectUserById(userId);
        if (user == null) {
            return Result.error("User not found");
        }

        user.setEmail(verificationToken.getNewValue());
        int rows = userMapper.updateUserById(user);

        if (rows > 0) {
            verificationTokenMapper.deleteTokenByUserIdAndType(userId, TOKEN_TYPE_EMAIL);
            return Result.success("Email updated successfully");
        } else {
            return Result.error("Failed to update email");
        }
    }

    @Override
    @Transactional
    public Result<String> verifyPhoneToken(Integer userId, String token) {
        VerificationToken verificationToken = verificationTokenMapper.selectTokenByUserIdTypeAndToken(
                userId, TOKEN_TYPE_PHONE, token);

        if (verificationToken == null) {
            return Result.error("Invalid verification code");
        }

        if (verificationToken.getExpiresAt().before(new Date())) {
            verificationTokenMapper.deleteTokenByUserIdAndType(userId, TOKEN_TYPE_PHONE);
            return Result.error("Verification code has expired");
        }

        User existingUser = userMapper.selectUserByPhone(verificationToken.getNewValue());
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            return Result.error("Phone number already in use by another user");
        }

        User user = userMapper.selectUserById(userId);
        if (user == null) {
            return Result.error("User not found");
        }

        user.setPhone(verificationToken.getNewValue());
        int rows = userMapper.updateUserById(user);

        if (rows > 0) {
            verificationTokenMapper.deleteTokenByUserIdAndType(userId, TOKEN_TYPE_PHONE);
            return Result.success("Phone number updated successfully");
        } else {
            return Result.error("Failed to update phone number");
        }
    }

    @Override
    public int cleanupExpiredTokens() {
        return verificationTokenMapper.deleteExpiredTokens();
    }

    private String generateSixDigitToken() {
        Random random = new Random();
        int token = 100000 + random.nextInt(900000);
        return String.valueOf(token);
    }

    @Override
    @Transactional
    public Result<Void> sendRegistrationCode(String email, String captchaToken) {
        // 1. 强力验证滑块 Token (防止接口被绕过滑块直接刷)
        if (!captchaService.validateCaptchaToken(captchaToken)) {
            return Result.error("人机验证已失效，请重试", "/api/v1/auth/send-code");
        }

        // 2. Redis 频率限流
        String rateKey = "rate:send-code:" + email;
        String dayLimitKey = "rate:send-code:day:" + email;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(rateKey))) {
            return Result.error("验证码发送过于频繁，请稍后再试", "/api/v1/auth/send-code");
        }

        String dayCountStr = redisTemplate.opsForValue().get(dayLimitKey);
        int dayCount = dayCountStr == null ? 0 : Integer.parseInt(dayCountStr);
        if (dayCount >= 5) {
            return Result.error("该邮箱今日验证验证码请求次数已达上限", "/api/v1/auth/send-code");
        }

        // 3. 原有逻辑：生成并保存验证码到数据库
        String token = generateSixDigitToken();
        Date expiresAt = new Date(System.currentTimeMillis() + REGISTER_TOKEN_EXPIRY_MINUTES * 60 * 1000);

        VerificationToken existingToken = verificationTokenMapper.selectTokenByNewValueAndType(email, TOKEN_TYPE_REGISTER);

        if (existingToken != null) {
            existingToken.setToken(token);
            existingToken.setExpiresAt(expiresAt);
            verificationTokenMapper.updateTokenByNewValueAndType(existingToken);
        } else {
            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setUserId(null); // Allow null for pre-registration
            verificationToken.setTokenType(TOKEN_TYPE_REGISTER);
            verificationToken.setNewValue(email);
            verificationToken.setToken(token);
            verificationToken.setExpiresAt(expiresAt);
            verificationTokenMapper.insertToken(verificationToken);
        }

        // 4. 更新限流状态
        redisTemplate.opsForValue().set(rateKey, "1", 60, java.util.concurrent.TimeUnit.SECONDS);
        redisTemplate.opsForValue().increment(dayLimitKey);
        if (dayCount == 0) {
            redisTemplate.expire(dayLimitKey, 24, java.util.concurrent.TimeUnit.HOURS);
        }

        // 5. 发送邮件
        boolean success = emailService.sendVerificationEmail(email, token);
        if (success) {
            return Result.successWithMessageAndPath("验证码发送成功", "/api/v1/auth/send-code");
        } else {
            return Result.error("邮件发送失败，请稍后重试", "/api/v1/auth/send-code");
        }
    }

    @Override
    @Transactional
    public Result<String> verifyRegistrationCode(String email, String code) {
        VerificationToken verificationToken = verificationTokenMapper.selectTokenByNewValueAndType(email, TOKEN_TYPE_REGISTER);

        if (verificationToken == null || !verificationToken.getToken().equals(code)) {
            return Result.error("验证码不正确");
        }

        if (verificationToken.getExpiresAt().before(new Date())) {
            verificationTokenMapper.deleteTokenByNewValueAndType(email, TOKEN_TYPE_REGISTER);
            return Result.error("验证码已过期");
        }

        // 验证成功后删除token防止被刷
        verificationTokenMapper.deleteTokenByNewValueAndType(email, TOKEN_TYPE_REGISTER);
        
        return Result.success("验证成功");
    }
}
