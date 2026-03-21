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

    private static final String VIEW_KEY_PATTERN = "headline:page_views:*";

    /**
     * 每 5 分钟同步一次浏览量到数据库
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void syncPageViews() {
        log.info("Starting page views sync job...");
        
        Set<String> keys = redisTemplate.keys(VIEW_KEY_PATTERN);
        if (keys == null || keys.isEmpty()) {
            log.info("No page views buffer found in Redis.");
            return;
        }

        List<HeadlineStatDTO> stats = new ArrayList<>();
        
        for (String key : keys) {
            try {
                // 获取当前增量值
                Object val = redisTemplate.opsForValue().get(key);
                if (val == null) continue;
                
                Long increment = ((Number) val).longValue();
                if (increment <= 0) {
                    redisTemplate.delete(key);
                    continue;
                }

                // 解析 hid
                String hidStr = key.substring(key.lastIndexOf(":") + 1);
                Integer hid = Integer.valueOf(hidStr);
                
                stats.add(new HeadlineStatDTO(hid, increment));
                
                // 处理完后删除Redis中的键（或者重置为0）
                // 采用删除策略，下次INCR会自动从0开始
                redisTemplate.delete(key);
                
            } catch (Exception e) {
                log.error("Error processing sync for key {}: {}", key, e.getMessage());
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
