package com.zhouyi.service;

import com.zhouyi.common.result.Result;

/**
 * Verification service interface for email and phone verification
 */
public interface VerificationService {

    /**
     * Generate email verification token
     * 
     * @param userId   user ID
     * @param newEmail new email address
     * @return result with verification token
     */
    Result<String> generateEmailVerificationToken(Integer userId, String newEmail);

    /**
     * Generate phone verification token
     * 
     * @param userId   user ID
     * @param newPhone new phone number
     * @return result with verification token
     */
    Result<String> generatePhoneVerificationToken(Integer userId, String newPhone);

    /**
     * Verify email token and update email
     * 
     * @param userId user ID
     * @param token  verification token
     * @return result of verification
     */
    Result<String> verifyEmailToken(Integer userId, String token);

    /**
     * Verify phone token and update phone
     * 
     * @param userId user ID
     * @param token  verification token
     * @return result of verification
     */
    Result<String> verifyPhoneToken(Integer userId, String token);

    /**
     * Clean up expired tokens
     * 
     * @return number of tokens deleted
     */
    int cleanupExpiredTokens();
}
