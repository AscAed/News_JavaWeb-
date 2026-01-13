package com.zhouyi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for admin user updates (status and role_id only)
 */
@Data
public class AdminUserUpdateDTO {

    @NotNull(message = "Status cannot be null")
    private Integer status;

    private Integer roleId;
}
