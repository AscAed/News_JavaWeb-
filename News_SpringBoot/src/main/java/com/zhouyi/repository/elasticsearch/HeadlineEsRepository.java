package com.zhouyi.repository.elasticsearch;

import com.zhouyi.entity.elasticsearch.HeadlineEsEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeadlineEsRepository extends ElasticsearchRepository<HeadlineEsEntity, Integer> {
    
    // 简单的派生查询：根据标题或内容搜索
    List<HeadlineEsEntity> findByTitleOrArticle(String title, String article);
}
