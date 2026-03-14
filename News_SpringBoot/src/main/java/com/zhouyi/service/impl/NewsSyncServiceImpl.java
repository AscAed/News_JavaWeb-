package com.zhouyi.service.impl;

import com.zhouyi.common.result.Result;
import com.zhouyi.entity.Headline;
import com.zhouyi.entity.elasticsearch.HeadlineEsEntity;
import com.zhouyi.entity.mongodb.NewsContent;
import com.zhouyi.mapper.HeadlineMapper;
import com.zhouyi.repository.elasticsearch.HeadlineEsRepository;
import com.zhouyi.service.NewsSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of NewsSyncService.
 */
@Service
@Slf4j
public class NewsSyncServiceImpl implements NewsSyncService {

    @Autowired
    private HeadlineMapper headlineMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HeadlineEsRepository headlineEsRepository;

    @Override
    public Result<String> fullSync() {
        log.info("Starting full Elasticsearch synchronization...");
        try {
            // 1. Clear current index
            headlineEsRepository.deleteAll();
            log.info("ES Index cleared.");

            // 2. Batch process all published news
            int pageSize = 100;
            int offset = 0;
            long totalSynced = 0;

            while (true) {
                // Fetch headlines batch
                List<Headline> headlines = headlineMapper.selectHeadlinesByPage(
                        offset, pageSize, null, null, null, null, null, null, null);
                
                if (headlines == null || headlines.isEmpty()) {
                    break;
                }

                List<HeadlineEsEntity> esEntities = new ArrayList<>();
                for (Headline h : headlines) {
                    // Only sync published news
                    if (h.getStatus() != 1) continue; 

                    HeadlineEsEntity esEntity = new HeadlineEsEntity();
                    esEntity.setHid(h.getHid());
                    esEntity.setTitle(h.getTitle());
                    esEntity.setTypeName(h.getTypeName());
                    esEntity.setType(h.getType());
                    esEntity.setPageViews(h.getPageViews());

                    // Get article content from MongoDB
                    NewsContent content = mongoTemplate.findById(h.getHid(), NewsContent.class);
                    if (content != null) {
                        esEntity.setArticle(content.getContent());
                    } else if (h.getMongodbDocumentId() != null && !h.getMongodbDocumentId().isEmpty()) {
                        content = mongoTemplate.findById(h.getMongodbDocumentId(), NewsContent.class);
                        if (content != null) {
                            esEntity.setArticle(content.getContent());
                        }
                    }

                    esEntities.add(esEntity);
                }

                if (!esEntities.isEmpty()) {
                    headlineEsRepository.saveAll(esEntities);
                    totalSynced += esEntities.size();
                    log.info("Synced batch: {} items", esEntities.size());
                }

                if (headlines.size() < pageSize) {
                    break;
                }
                offset += pageSize;
            }

            log.info("Full synchronization completed. Total synced: {}", totalSynced);
            return Result.success("同步完成，成功导入 " + totalSynced + " 条新闻到Elasticsearch");
        } catch (Exception e) {
            log.error("Elasticsearch synchronization failed", e);
            return Result.error("同步失败：" + e.getMessage());
        }
    }
}
