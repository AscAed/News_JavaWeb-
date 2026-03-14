package com.zhouyi.service;

import com.zhouyi.common.result.Result;

/**
 * Service for synchronizing data between MySQL/MongoDB and Elasticsearch.
 */
public interface NewsSyncService {
    
    /**
     * Rebuilds the Elasticsearch index by synchronizing all published news items.
     * @return Result containing success message and count of synced items
     */
    Result<String> fullSync();
}
