package com.zhouyi.service.impl;

import com.zhouyi.entity.OutboxMessage;
import com.zhouyi.mapper.OutboxMapper;
import com.zhouyi.service.OutboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OutboxServiceImpl implements OutboxService {

    @Autowired
    private OutboxMapper outboxMapper;

    @Override
    @Transactional
    public void saveEsSyncMessage(Integer hid, String operationType) {
        String payload = String.format("{\"hid\":%d, \"op\":\"%s\"}", hid, operationType);
        OutboxMessage message = OutboxMessage.builder()
                .category("HEADLINE_ES_SYNC")
                .payload(payload)
                .status("PENDING")
                .retryCount(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        outboxMapper.insert(message);
    }

    @Override
    public List<OutboxMessage> fetchPendingMessages(String category, int limit) {
        return outboxMapper.selectPending(category, limit);
    }

    @Override
    @Transactional
    public void markAsSent(Long id) {
        outboxMapper.updateStatus(id, "SENT");
    }

    @Override
    @Transactional
    public void markAsFailed(Long id) {
        OutboxMessage message = outboxMapper.selectById(id);
        if (message != null) {
            outboxMapper.updateRetry(id, "FAILED", message.getRetryCount() + 1);
        }
    }
}
