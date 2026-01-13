package com.zhouyi.repository.mongo;

import com.zhouyi.entity.mongo.RssArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * RSS文章MongoDB Repository
 */
@Repository
public interface RssArticleRepository extends MongoRepository<RssArticle, String> {

    /**
     * 根据链接查找文章
     */
    Optional<RssArticle> findByLink(String link);

    /**
     * 根据GUID查找文章
     */
    Optional<RssArticle> findByGuid(String guid);

    /**
     * 根据订阅源ID查找文章
     */
    List<RssArticle> findBySubscriptionId(String subscriptionId);

    /**
     * 根据订阅源ID分页查找文章
     */
    Page<RssArticle> findBySubscriptionId(String subscriptionId, Pageable pageable);

    /**
     * 根据分类查找文章
     */
    List<RssArticle> findByCategories(String category);

    /**
     * 根据多个分类查找文章
     */
    @Query("{ 'categories': { '$in': ?0 } }")
    List<RssArticle> findByCategoriesIn(List<String> categories);

    /**
     * 根据作者查找文章
     */
    List<RssArticle> findByAuthor(String author);

    /**
     * 根据发布时间范围查找文章
     */
    @Query("{ 'pubDate': { '$gte': ?0, '$lte': ?1 } }")
    List<RssArticle> findByPubDateBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 根据标题模糊查询
     */
    @Query("{ 'title': { '$regex': ?0, '$options': 'i' } }")
    List<RssArticle> findByTitleContaining(String title);

    /**
     * 全文搜索（标题和内容）
     */
    @Query("{ '$or': [ { 'title': { '$regex': ?0, '$options': 'i' } }, { 'contentText': { '$regex': ?0, '$options': 'i' } }, { 'description': { '$regex': ?0, '$options': 'i' } } ] }")
    List<RssArticle> findByContentContaining(String keyword);

    /**
     * 查找未读文章
     */
    List<RssArticle> findByIsReadFalse();

    /**
     * 查找已收藏文章
     */
    List<RssArticle> findByIsFavoriteTrue();

    /**
     * 根据语言查找文章
     */
    List<RssArticle> findByLanguage(String language);

    /**
     * 根据重要度评分范围查找文章
     */
    @Query("{ 'importanceScore': { '$gte': ?0, '$lte': ?1 } }")
    List<RssArticle> findByImportanceScoreBetween(Double min, Double max);

    /**
     * 统计订阅源的文章数量
     */
    long countBySubscriptionId(String subscriptionId);

    /**
     * 统计分类的文章数量
     */
    long countByCategories(String category);

    /**
     * 查找最近的文章
     */
    List<RssArticle> findTop10ByOrderByPubDateDesc();

    /**
     * 查找最新的文章（按创建时间）
     */
    List<RssArticle> findTop10ByOrderByCreatedAtDesc();

    /**
     * 根据关键词查找文章
     */
    @Query("{ 'keywords': { '$in': ?0 } }")
    List<RssArticle> findByKeywordsIn(List<String> keywords);

    /**
     * 删除指定时间之前的文章
     */
    @Query(value = "{ 'createdAt': { '$lt': ?0 } }", delete = true)
    void deleteByCreatedAtBefore(LocalDateTime date);

    /**
     * 根据发布时间查找之前的文章
     */
    List<RssArticle> findByPubDateBefore(LocalDateTime date);
}
