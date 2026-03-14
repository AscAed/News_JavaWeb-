package com.zhouyi.service.impl;

import com.zhouyi.dto.SearchResultDTO;
import com.zhouyi.entity.elasticsearch.HeadlineEsEntity;
import com.zhouyi.service.NewsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NewsSearchServiceImpl implements NewsSearchService {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Override
    public SearchResultDTO globalSearch(String keyword, Integer typeId, Integer page, Integer pageSize) {
        // 构建高亮查询配置
        HighlightField titleField = new HighlightField("title");
        HighlightField articleField = new HighlightField("article");
        
        Highlight highlight = new Highlight(
                HighlightParameters.builder()
                        .withPreTags("<em style='color:red'>")
                        .withPostTags("</em>")
                        .build(),
                List.of(titleField, articleField)
        );

        // 构建分面/分页原生查询
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> {
                    // 关键词匹配
                    b.must(m -> m.multiMatch(mm -> mm.fields("title", "article").query(keyword)));
                    // 类型过滤 (如果提供且不为0)
                    if (typeId != null && typeId != 0) {
                        b.filter(f -> f.term(t -> t.field("type").value(typeId)));
                    }
                    return b;
                }))
                .withPageable(PageRequest.of(page - 1, pageSize))
                .withHighlightQuery(new HighlightQuery(highlight, HeadlineEsEntity.class))
                .build();

        SearchHits<HeadlineEsEntity> searchHits = elasticsearchOperations.search(query, HeadlineEsEntity.class);

        // 提取并处理搜索命中的结果
        List<HeadlineEsEntity> items = new ArrayList<>();
        for (SearchHit<HeadlineEsEntity> hit : searchHits) {
            HeadlineEsEntity content = hit.getContent();
            
            // 获取高亮字段并替换到结果实体中
            Map<String, List<String>> highlightFields = hit.getHighlightFields();
            if (highlightFields.containsKey("title")) {
                content.setTitle(highlightFields.get("title").get(0));
            }
            if (highlightFields.containsKey("article")) {
                content.setArticle(highlightFields.get("article").get(0));
            }
            
            items.add(content);
        }
        
        long total = searchHits.getTotalHits();
        int totalPages = (int) Math.ceil((double) total / pageSize);

        return new SearchResultDTO(items, total, page, pageSize, totalPages);
    }
}
