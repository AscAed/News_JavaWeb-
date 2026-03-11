package com.zhouyi.service;

import java.util.List;
import java.util.Map;

/**
 * RSS服务接口
 */
public interface RssService {

    /**
     * 采集并保存RSS订阅内容
     *
     * @param subscriptionId 订阅源ID
     * @param section        文章分区 (例如: china, singapore)
     * @return 采集结果统计
     */
    Map<String, Object> fetchAndSave(Long subscriptionId, String section);

    /**
     * 采集并保存RSS订阅内容 (默认无分区)
     */
    default Map<String, Object> fetchAndSave(Long subscriptionId) {
        return fetchAndSave(subscriptionId, null);
    }

    /**
     * 创建 RSS 订阅源
     *
     * @param createDTO 创建参数
     * @return 创建结果
     */
    Map<String, Object> createSubscription(com.zhouyi.dto.RssSubscriptionCreateDTO createDTO);

    /**
     * 查询所有启用的订阅源
     *
     * @return 启用的订阅源列表
     */
    List<com.zhouyi.entity.RssSubscription> listActiveSubscriptions();
}
