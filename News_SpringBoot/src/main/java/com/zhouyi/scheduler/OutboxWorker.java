package com.zhouyi.scheduler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhouyi.entity.Headline;
import com.zhouyi.entity.OutboxMessage;
import com.zhouyi.entity.elasticsearch.HeadlineEsEntity;
import com.zhouyi.mapper.HeadlineMapper;
import com.zhouyi.repository.elasticsearch.HeadlineEsRepository;
import com.zhouyi.service.OutboxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class OutboxWorker {

    @Autowired
    private OutboxService outboxService;

    @Autowired
    private HeadlineEsRepository headlineEsRepository;

    @Autowired
    private HeadlineMapper headlineMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Scheduled(fixedDelay = 5000) // 5s interval
    public void processOutbox() {
        List<OutboxMessage> messages = outboxService.fetchPendingMessages("HEADLINE_ES_SYNC", 10);
        if (messages.isEmpty()) return;

        log.info("OutboxWorker: Processing {} pending messages", messages.size());

        for (OutboxMessage message : messages) {
            try {
                JsonNode payload = objectMapper.readTree(message.getPayload());
                Integer hid = payload.get("hid").asInt();
                String op = payload.get("op").asText();

                if ("SAVE".equals(op)) {
                    Headline headline = headlineMapper.selectHeadlineById(hid);
                    if (headline != null && headline.getStatus() == 1) {
                        HeadlineEsEntity esEntity = new HeadlineEsEntity();
                        esEntity.setHid(headline.getHid());
                        esEntity.setTitle(headline.getTitle());
                        esEntity.setArticle(headline.getSummary()); // Use summary if article not available directly
                        esEntity.setTypeName(headline.getTypeName());
                        esEntity.setType(headline.getType());
                        esEntity.setPageViews(headline.getPageViews());
                        
                        headlineEsRepository.save(esEntity);
                    }
                } else if ("DELETE".equals(op)) {
                    headlineEsRepository.deleteById(hid);
                }

                outboxService.markAsSent(message.getId());
                log.info("OutboxWorker: Successfully processed message id [{}] for hid [{}]", message.getId(), hid);

            } catch (Exception e) {
                log.error("OutboxWorker: Failed to process message id [{}]: {}", message.getId(), e.getMessage());
                outboxService.markAsFailed(message.getId());
            }
        }
    }
}
