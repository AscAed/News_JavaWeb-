package com.zhouyi.dto;

import com.zhouyi.entity.elasticsearch.HeadlineEsEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 搜索结果包装类，支持分页
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDTO {
    private List<HeadlineEsEntity> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
