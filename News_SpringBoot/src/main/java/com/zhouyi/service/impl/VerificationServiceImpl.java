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

/**
 * Verification service implementation
 */
@Service
public class VerificationServiceImpl implements VerificationService {

    @Autowired
    private VerificationTokenMapper verificationTokenMapper;

    @Autowired
    private UserMapper userMapper;

    private static final int TOKEN_EXPIRY_MINUTES = 15;
    private static final String TOKEN_TYPE_EMAIL = "EMAIL";
    private static final String TOKEN_TYPE_PHONE = "PHONE";

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
}
