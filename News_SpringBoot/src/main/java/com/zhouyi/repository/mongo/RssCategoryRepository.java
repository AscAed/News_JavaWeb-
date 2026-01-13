package com.zhouyi.repository.mongo;

import com.zhouyi.entity.mongo.RssCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * RSS分类MongoDB Repository
 */
@Repository
public interface RssCategoryRepository extends MongoRepository<RssCategory, String> {

    /**
     * 根据名称查找分类
     */
    Optional<RssCategory> findByName(String name);

    /**
     * 查找所有启用的分类
     */
    List<RssCategory> findByEnabledTrue();

    /**
     * 根据名称模糊查询
     */
    @Query("{ 'name': { '$regex': ?0, '$options': 'i' } }")
    List<RssCategory> findByNameContaining(String name);

    /**
     * 根据文章数量排序查找分类
     */
    List<RssCategory> findByOrderByArticleCountDesc();

    /**
     * 查找文章数量大于指定值的分类
     */
    @Query("{ 'articleCount': { '$gt': ?0 } }")
    List<RssCategory> findByArticleCountGreaterThan(Integer count);

    /**
     * 统计启用的分类数量
     */
    long countByEnabledTrue();

    /**
     * 查找热门分类（按文章数量排序）
     */
    @Query(value = "{ 'enabled': true }", sort = "{ 'articleCount': -1 }")
    List<RssCategory> findTop10ByOrderByArticleCountDesc();

    /**
     * 查找未使用的分类（文章数量为0）
     */
    List<RssCategory> findByArticleCountEquals(Integer count);
}
