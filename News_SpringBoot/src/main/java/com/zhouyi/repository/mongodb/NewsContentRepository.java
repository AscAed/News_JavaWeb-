package com.zhouyi.repository.mongodb;

import com.zhouyi.entity.mongodb.NewsContent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 新闻内容MongoDB仓库
 */
@Repository
public interface NewsContentRepository extends MongoRepository<NewsContent, String> {
    
    /**
     * 根据新闻ID查找内容
     */
    NewsContent findByNewsId(Integer newsId);
    
    /**
     * 根据标题模糊搜索
     */
    List<NewsContent> findByTitleContainingIgnoreCase(String title);
    
    /**
     * 根据内容模糊搜索
     */
    List<NewsContent> findByContentContainingIgnoreCase(String content);
    
    /**
     * 根据关键词搜索
     */
    @Query("{'keywords': {$regex: ?0, $options: 'i'}}")
    List<NewsContent> findByKeywords(String keyword);
    
    /**
     * 根据状态查找
     */
    List<NewsContent> findByStatus(Integer status);
    
    /**
     * 全文搜索（标题和内容）
     */
    @Query("{$or: [{'title': {$regex: ?0, $options: 'i'}}, {'content': {$regex: ?0, $options: 'i'}}]}")
    List<NewsContent> fullTextSearch(String keyword);
    
    /**
     * 统计新闻数量
     */
    long countByStatus(Integer status);
}
