package com.zhouyi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * DTO for phone update request
 */
@Data
public class PhoneUpdateRequestDTO {

    @NotBlank(message = "New phone cannot be empty")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "Invalid phone format")
    private String newPhone;
}
