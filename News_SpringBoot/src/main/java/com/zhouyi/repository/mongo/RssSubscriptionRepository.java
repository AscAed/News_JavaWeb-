package com.zhouyi.repository.mongo;

import com.zhouyi.entity.mongo.RssSubscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * RSS订阅源MongoDB Repository
 */
@Repository
public interface RssSubscriptionRepository extends MongoRepository<RssSubscription, String> {

    /**
     * 根据URL查找订阅源
     */
    Optional<RssSubscription> findByUrl(String url);

    /**
     * 根据名称查找订阅源
     */
    Optional<RssSubscription> findByName(String name);

    /**
     * 查找所有启用的订阅源
     */
    List<RssSubscription> findByEnabledTrue();

    /**
     * 根据采集状态查找订阅源
     */
    List<RssSubscription> findByFetchStatus(String fetchStatus);

    /**
     * 查找需要采集的订阅源（超过指定时间未采集）
     */
    @Query("{ 'enabled': true, '$or': [ { 'lastFetchedAt': { '$lt': ?0 } }, { 'lastFetchedAt': null } ] }")
    List<RssSubscription> findSubscriptionsToFetch(LocalDateTime threshold);

    /**
     * 统计启用的订阅源数量
     */
    long countByEnabledTrue();

    /**
     * 根据名称模糊查询
     */
    @Query("{ 'name': { '$regex': ?0, '$options': 'i' } }")
    List<RssSubscription> findByNameContaining(String name);

    /**
     * 根据语言查找订阅源
     */
    List<RssSubscription> findByLanguage(String language);
}
