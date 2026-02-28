package com.zhouyi.mapper;

import com.zhouyi.entity.RssSubscription;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface RssSubscriptionMapper {

    /**
     * 根据ID查询订阅源
     */
    RssSubscription findById(Long id);

    /**
     * 查询所有订阅源
     */
    List<RssSubscription> findAll();

    /**
     * 查询所有启用的订阅源
     */
    List<RssSubscription> findAllActive();

    /**
     * 根据分类查询订阅源
     */
    List<RssSubscription> findByCategory(@Param("category") String category);

    /**
     * 插入订阅源
     */
    int insert(RssSubscription subscription);

    /**
     * 更新订阅源
     */
    int update(RssSubscription subscription);

    /**
     * 更新最后抓取时间
     */
    void updateLastFetchedTime(@Param("id") Long id, @Param("time") LocalDateTime time);

    /**
     * 更新抓取状态
     */
    void updateFetchStatus(@Param("id") Long id, @Param("status") String status,
                           @Param("errorMessage") String errorMessage);

    /**
     * 更新文章总数
     */
    void updateTotalArticles(@Param("id") Long id, @Param("totalArticles") Integer totalArticles);

    /**
     * 删除订阅源
     */
    int deleteById(@Param("id") Long id);

    /**
     * 统计订阅源总数
     */
    long count();

    /**
     * 统计启用的订阅源数量
     */
    long countActive();
}
