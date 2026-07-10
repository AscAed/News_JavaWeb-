package com.zhouyi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新闻统计数据传输对象
 * 用于批量更新浏览量
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeadlineStatDTO {
    /**
     * 新闻ID
     */
    private Integer hid;
    
    /**
     * 待增加的浏览量
     */
    private Long pageViews;
}
