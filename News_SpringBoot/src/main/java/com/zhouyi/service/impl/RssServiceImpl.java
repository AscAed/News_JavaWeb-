package com.zhouyi.service.impl;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.zhouyi.entity.RssFeedItem;
import com.zhouyi.entity.RssSubscription;
import com.zhouyi.mapper.RssFeedItemMapper;
import com.zhouyi.mapper.RssSubscriptionMapper;
import com.zhouyi.service.RssService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

@Service
public class RssServiceImpl implements RssService {

    private static final Logger log = LoggerFactory.getLogger(RssServiceImpl.class);

    @Autowired
    private RssSubscriptionMapper rssSubscriptionMapper;

    @Autowired
    private RssFeedItemMapper rssFeedItemMapper;

    @Override
    @Transactional
    public Map<String, Object> fetchAndSave(Long subscriptionId) {
        RssSubscription subscription = rssSubscriptionMapper.findById(subscriptionId);
        if (subscription == null) {
            throw new RuntimeException("RSS订阅源不存在: " + subscriptionId);
        }

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> newArticlesList = new ArrayList<>();
        int newCount = 0;
        int fetchedCount = 0;

        try {
            log.info("开始采集RSS: {}", subscription.getUrl());
            URL feedUrl = new URL(subscription.getUrl());

            // 尝试多种User-Agent策略
            String[] userAgents = {
                    "Mozilla/5.0 (compatible; RSS-Reader/1.0; +https://example.com/rss)",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:121.0) Gecko/20100101 Firefox/121.0",
                    "curl/8.0.0"
            };

            SyndFeed feed = null;
            Exception lastException = null;

            for (int i = 0; i < userAgents.length; i++) {
                try {
                    log.debug("尝试使用User-Agent {}: {}", i + 1, userAgents[i]);

                    URLConnection connection = feedUrl.openConnection();
                    connection.setRequestProperty("User-Agent", userAgents[i]);
                    connection.setRequestProperty("Accept", "application/rss+xml, application/xml, text/xml; q=0.9, */*; q=0.1");
                    connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
                    connection.setRequestProperty("Connection", "keep-alive");
                    connection.setRequestProperty("Cache-Control", "no-cache");
                    connection.setRequestProperty("Pragma", "no-cache");
                    if (i > 0) {
                        connection.setRequestProperty("Referer", "https://rsshub.app/");
                    }
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(20000);

                    // 获取输入流并处理gzip压缩
                    InputStream inputStream = connection.getInputStream();
                    String encoding = connection.getContentEncoding();
                    if (encoding != null && encoding.contains("gzip")) {
                        inputStream = new GZIPInputStream(inputStream);
                        log.debug("使用gzip解压缩响应内容");
                    }

                    SyndFeedInput input = new SyndFeedInput();
                    feed = input.build(new XmlReader(inputStream));

                    log.info("成功使用User-Agent {} 获取RSS feed", i + 1);
                    break;

                } catch (Exception e) {
                    lastException = e;
                    log.warn("User-Agent {} 失败: {}", i + 1, e.getMessage());
                    if (i < userAgents.length - 1) {
                        try {
                            Thread.sleep(1000); // 等待1秒后重试
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }

            if (feed == null) {
                throw new RuntimeException("所有User-Agent尝试都失败了，最后一个错误: " +
                        (lastException != null ? lastException.getMessage() : "未知错误"));
            }

            fetchedCount = feed.getEntries().size();
            Date now = new Date();

            for (SyndEntry entry : feed.getEntries()) {
                String link = entry.getLink();
                String guid = entry.getUri(); // Rome matches GUID to URI often

                // Check duplication
                if (rssFeedItemMapper.countByLinkOrGuid(link, guid) > 0) {
                    continue;
                }

                RssFeedItem item = new RssFeedItem();
                item.setSubscriptionId(subscriptionId);
                item.setTitle(entry.getTitle());
                item.setLink(link);
                item.setGuid(guid);
                item.setAuthor(entry.getAuthor());

                // Description handling
                if (entry.getDescription() != null) {
                    item.setDescription(entry.getDescription().getValue());
                } else if (!entry.getContents().isEmpty()) {
                    item.setDescription(entry.getContents().get(0).getValue());
                }

                // PubDate handling
                Date pubDate = entry.getPublishedDate();
                if (pubDate == null) {
                    pubDate = entry.getUpdatedDate();
                }
                if (pubDate == null) {
                    pubDate = now;
                }
                item.setPubDate(pubDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

                // Save
                rssFeedItemMapper.insert(item);
                newCount++;

                // Add to result list (simplified DTO)
                Map<String, Object> articleMap = new HashMap<>();
                articleMap.put("title", item.getTitle());
                articleMap.put("description", item.getDescription());
                articleMap.put("link", item.getLink());
                articleMap.put("pub_date", item.getPubDate());
                articleMap.put("guid", item.getGuid());
                newArticlesList.add(articleMap);
            }

            // Update subscription last fetched time
            rssSubscriptionMapper.updateLastFetchedTime(subscriptionId, LocalDateTime.now());

            log.info("RSS采集完成: {}, 获取条目: {}, 新增: {}", subscription.getName(), fetchedCount, newCount);

        } catch (Exception e) {
            log.error("RSS采集失败: {}", e.getMessage(), e);
            throw new RuntimeException("RSS采集失败: " + e.getMessage());
        }

        result.put("subscription_id", subscriptionId);
        result.put("fetched_count", fetchedCount);
        result.put("new_articles", newCount);
        result.put("fetch_time", LocalDateTime.now());
        result.put("articles", newArticlesList);

        return result;
    }
}
