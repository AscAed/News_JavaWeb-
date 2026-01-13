package com.zhouyi.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for user profile updates (username and avatar)
 */
@Data
public class UserProfileUpdateDTO {

    @Size(min = 2, max = 20, message = "Username must be between 2 and 20 characters")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9_]{2,20}$", message = "Username can only contain Chinese characters, letters, numbers and underscores")
    private String username;

    @Size(max = 500, message = "Avatar URL cannot exceed 500 characters")
    private String avatar;
}
