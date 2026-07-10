package com.zhouyi.service;

import com.zhouyi.entity.OutboxMessage;
import java.util.List;

public interface OutboxService {
    void saveEsSyncMessage(Integer hid, String operationType);
    List<OutboxMessage> fetchPendingMessages(String category, int limit);
    void markAsSent(Long id);
    void markAsFailed(Long id);
}
