package com.zhouyi.scheduler;

import com.zhouyi.entity.RssSubscription;
import com.zhouyi.mapper.RssSubscriptionMapper;
import com.zhouyi.service.HybridRssService;
import com.zhouyi.component.NewsMetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * RSS 异步抓取调度器
 * 将 RSS 抓取逻辑从请求链路中剥离，周期性自动更新
 */
@Component
public class RssSyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(RssSyncScheduler.class);

    @Autowired
    private RssSubscriptionMapper rssSubscriptionMapper;

    @Autowired
    private HybridRssService hybridRssService;

    @Autowired
    private NewsMetricsService newsMetricsService;

    /**
     * 每 30 分钟同步一次所有激活的 RSS 订阅源
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void syncAllRss() {
        newsMetricsService.recordRssSyncDuration(() -> {
            log.info("Starting background RSS synchronization job...");

            List<RssSubscription> activeSubs = rssSubscriptionMapper.findAllActive();
            if (activeSubs == null || activeSubs.isEmpty()) {
                log.info("No active RSS subscriptions found.");
                return;
            }

            for (RssSubscription sub : activeSubs) {
                try {
                    log.info("Processing RSS subscription: {} ({})", sub.getName(), sub.getUrl());

                    if (sub.getUrl() != null && sub.getUrl().contains("zaobao")) {
                        String[] sections = {"china", "world", "singapore"};
                        for (String section : sections) {
                            log.info("Triggering async fetch for {} - section: {}", sub.getName(), section);
                            hybridRssService.fetchAndSave(sub.getId(), section);
                        }
                    } else {
                        log.info("Triggering async fetch for {}", sub.getName());
                        hybridRssService.fetchAndSave(sub.getId(), null);
                    }
                } catch (Exception e) {
                    log.error("Failed to trigger background sync for subscription {}: {}", sub.getId(), e.getMessage());
                }
            }

            log.info("Background RSS synchronization job triggered for all active sources.");
        });
    }
}
