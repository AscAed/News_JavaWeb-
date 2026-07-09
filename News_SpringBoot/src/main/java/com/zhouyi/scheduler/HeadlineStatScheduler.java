package com.zhouyi.scheduler;

import com.zhouyi.dto.HeadlineStatDTO;
import com.zhouyi.mapper.HeadlineMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 新闻统计数据同步调度器
 * 将 Redis 中的浏览量定时回写到 MySQL
 */
@Component
public class HeadlineStatScheduler {

    private static final Logger log = LoggerFactory.getLogger(HeadlineStatScheduler.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private HeadlineMapper headlineMapper;

    private static final String VIEW_HASH_KEY = "headline:page_views:hash";

    /**
     * 每 5 分钟同步一次浏览量到数据库
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void syncPageViews() {
        log.info("Starting page views sync job...");
        
        // 获取 Hash 中所有的缓冲数据
        java.util.Map<Object, Object> entries = redisTemplate.opsForHash().entries(VIEW_HASH_KEY);
        if (entries == null || entries.isEmpty()) {
            log.info("No page views buffer found in Redis Hash.");
            return;
        }

        List<HeadlineStatDTO> stats = new ArrayList<>();
        
        for (java.util.Map.Entry<Object, Object> entry : entries.entrySet()) {
            try {
                // 解析 hid
                String hidStr = entry.getKey().toString();
                Integer hid = Integer.valueOf(hidStr);
                
                // 获取当前增量值
                Long increment = ((Number) entry.getValue()).longValue();
                if (increment <= 0) {
                    redisTemplate.opsForHash().delete(VIEW_HASH_KEY, hidStr);
                    continue;
                }

                stats.add(new HeadlineStatDTO(hid, increment));
                
                // 处理完后从Hash中删除该字段
                redisTemplate.opsForHash().delete(VIEW_HASH_KEY, hidStr);
                
            } catch (Exception e) {
                log.error("Error processing sync for hid {}: {}", entry.getKey(), e.getMessage());
            }

            // 分批处理，防止 SQL 过长
            if (stats.size() >= 50) {
                flushStats(stats);
                stats.clear();
            }
        }

        if (!stats.isEmpty()) {
            flushStats(stats);
        }
        
        log.info("Page views sync job completed.");
    }

    private void flushStats(List<HeadlineStatDTO> stats) {
        try {
            log.info("Flushing {} headline stats to database...", stats.size());
            headlineMapper.updatePageViewsBatch(stats);
        } catch (Exception e) {
            log.error("Failed to batch update page views to MySQL: {}", e.getMessage());
        }
    }
}
