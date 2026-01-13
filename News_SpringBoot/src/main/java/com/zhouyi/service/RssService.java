package com.zhouyi.service;

import java.util.Map;

/**
 * RSS服务接口
 */
public interface RssService {

    /**
     * 采集并保存RSS订阅内容
     *
     * @param subscriptionId 订阅源ID
     * @return 采集结果统计
     */
    Map<String, Object> fetchAndSave(Long subscriptionId);
}
