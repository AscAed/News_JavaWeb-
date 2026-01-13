package com.zhouyi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for email update request
 */
@Data
public class EmailUpdateRequestDTO {

    @NotBlank(message = "New email cannot be empty")
    @Email(message = "Invalid email format")
    private String newEmail;
}
