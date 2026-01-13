package com.zhouyi.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

/**
 * Verification token entity for email and phone verification
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken {
    private Integer id;
    private Integer userId;
    private String tokenType;
    private String newValue;
    private String token;
    private Date expiresAt;
    private Date createdAt;
}
