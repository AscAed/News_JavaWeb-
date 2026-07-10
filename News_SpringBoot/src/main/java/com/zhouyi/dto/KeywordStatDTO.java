package com.zhouyi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 关键词统计 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeywordStatDTO {
    private String name;  // 词名
    private Long value;   // 频次
}
