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

    // Find article by its linked MySQL headline ID
    RssArticle findByMysqlHeadlineId(Integer mysqlHeadlineId);

    // Find articles that haven't been synced to MySQL yet
    List<RssArticle> findByMysqlHeadlineIdIsNull();

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

    // ========== Hybrid Storage Enhancement Methods ==========

    /**
     * 根据内容哈希查找文章（用于去重）
     */
    Optional<RssArticle> findByContentHash(String contentHash);

    /**
     * 根据订阅源ID和内容哈希查找文章
     */
    Optional<RssArticle> findBySubscriptionIdAndContentHash(String subscriptionId, String contentHash);

    /**
     * 查找包含特定搜索关键词的文章
     */
    @Query("{ 'searchKeywords': { '$in': ?0 } }")
    List<RssArticle> findBySearchKeywordsIn(List<String> keywords);

    /**
     * 全文搜索（使用searchText字段）
     */
    @Query("{ '$text': { '$search': ?0 } }")
    List<RssArticle> searchByText(String searchText);

    /**
     * 全文搜索（分页）
     */
    @Query("{ '$text': { '$search': ?0 } }")
    Page<RssArticle> searchByText(String searchText, Pageable pageable);

    /**
     * 查找未同步到Elasticsearch的文章
     */
    @Query("{ '$or': [ { 'esIndexed': false }, { 'esIndexed': null } ] }")
    List<RssArticle> findNotIndexedToElasticsearch();

    /**
     * 查找未同步到Elasticsearch的文章（分页）
     */
    @Query("{ '$or': [ { 'esIndexed': false }, { 'esIndexed': null } ] }")
    Page<RssArticle> findNotIndexedToElasticsearch(Pageable pageable);

    /**
     * 更新文章的Elasticsearch同步状态
     */
    @Query(value = "{ '_id': ?0 }", fields = "{ 'esIndexed': 1, 'esIndexedAt': 1 }")
    void updateElasticsearchSyncStatus(String id, boolean esIndexed, LocalDateTime esIndexedAt);

    /**
     * 根据版本号查找文章
     */
    List<RssArticle> findByVersion(Integer version);

    /**
     * 查找最新版本的文章（版本号大于指定值）
     */
    @Query("{ 'version': { '$gt': ?0 } }")
    List<RssArticle> findByVersionGreaterThan(Integer version);

    /**
     * 统计特定订阅源的文章总字数
     */
    @Query(value = "{ 'subscriptionId': ?0 }", fields = "{ 'wordCount': 1 }")
    List<RssArticle> findWordCountsBySubscriptionId(String subscriptionId);

    /**
     * 查找高重要度文章（分页）
     */
    @Query("{ 'importanceScore': { '$gte': ?0 } }")
    Page<RssArticle> findHighImportanceArticles(Double minScore, Pageable pageable);

    /**
     * 根据分类查找文章（分页）
     */
    Page<RssArticle> findByCategories(String category, Pageable pageable);

    /**
     * 查找最近浏览的文章
     */
    @Query("{ 'lastViewedAt': { '$ne': null } }")
    List<RssArticle> findRecentlyViewed(Pageable pageable);

    /**
     * 增加文章浏览次数
     */
    @Query(value = "{ '_id': ?0 }", fields = "{ 'viewCount': 1 }")
    void incrementViewCount(String id);

    /**
     * 增加文章分享次数
     */
    @Query(value = "{ '_id': ?0 }", fields = "{ 'shareCount': 1 }")
    void incrementShareCount(String id);
}
