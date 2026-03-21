package com.zhouyi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxMessage {
    private Long id;
    private String category;
    private String payload;
    private String status;
    private Integer retryCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
