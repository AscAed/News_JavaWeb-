package com.zhouyi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

/**
 * 收藏备注更新DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteUpdateNoteDTO {
    
    @Size(max = 200, message = "收藏备注不能超过200字符")
    private String note;
}
