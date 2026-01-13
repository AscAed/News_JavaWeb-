package com.zhouyi.service.mongo.impl;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.zhouyi.entity.mongo.RssArticle;
import com.zhouyi.entity.mongo.RssCategory;
import com.zhouyi.entity.mongo.RssSubscription;
import com.zhouyi.repository.mongo.RssArticleRepository;
import com.zhouyi.repository.mongo.RssCategoryRepository;
import com.zhouyi.repository.mongo.RssSubscriptionRepository;
import com.zhouyi.service.mongo.RssMongoService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * RSS MongoDB服务实现类
 */
@Slf4j
@Service
public class RssMongoServiceImpl implements RssMongoService {

    @Autowired
    private RssSubscriptionRepository subscriptionRepository;

    @Autowired
    private RssArticleRepository articleRepository;

    @Autowired
    private RssCategoryRepository categoryRepository;

    @Override
    @Transactional
    public Map<String, Object> fetchAndSaveToMongo(String subscriptionId) {
        RssSubscription subscription = subscriptionRepository.findById(subscriptionId).orElse(null);
        if (subscription == null) {
            throw new RuntimeException("RSS订阅源不存在: " + subscriptionId);
        }

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> newArticlesList = new ArrayList<>();
        int newCount = 0;
        int fetchedCount = 0;
        List<String> allCategories = new ArrayList<>();

        try {
            log.info("开始采集RSS到MongoDB: {}", subscription.getUrl());
            URL feedUrl = new URL(subscription.getUrl());

            // 使用多种User-Agent策略
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
                    connection.setRequestProperty("Accept",
                            "application/rss+xml, application/xml, text/xml; q=0.9, */*; q=0.1");
                    connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
                    connection.setRequestProperty("Connection", "keep-alive");
                    connection.setRequestProperty("Cache-Control", "no-cache");
                    connection.setRequestProperty("Pragma", "no-cache");
                    if (i > 0) {
                        connection.setRequestProperty("Referer", "https://rsshub.app/");
                    }
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(20000);

                    // 处理gzip压缩
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
                            Thread.sleep(1000);
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

            // 更新订阅源信息
            updateSubscriptionFromFeed(subscription, feed);
            subscriptionRepository.save(subscription);

            fetchedCount = feed.getEntries().size();
            Date now = new Date();

            for (SyndEntry entry : feed.getEntries()) {
                String link = entry.getLink();
                String guid = entry.getUri();

                // 检查重复
                if (articleRepository.findByLink(link).isPresent() ||
                        articleRepository.findByGuid(guid).isPresent()) {
                    continue;
                }

                // 提取分类信息
                List<String> categories = extractCategories(entry);
                allCategories.addAll(categories);

                // 创建文章实体
                RssArticle article = createArticleFromEntry(entry, subscription, categories);

                // 保存文章
                articleRepository.save(article);
                newCount++;

                // 添加到结果列表
                Map<String, Object> articleMap = new HashMap<>();
                articleMap.put("title", article.getTitle());
                articleMap.put("description", article.getDescription());
                articleMap.put("link", article.getLink());
                articleMap.put("pub_date", article.getPubDate());
                articleMap.put("categories", article.getCategories());
                articleMap.put("guid", article.getGuid());
                newArticlesList.add(articleMap);
            }

            // 更新分类统计
            updateCategoryStatistics(allCategories);

            log.info("RSS采集到MongoDB完成: {}, 获取条目: {}, 新增: {}",
                    subscription.getName(), fetchedCount, newCount);

        } catch (Exception e) {
            log.error("RSS采集到MongoDB失败: {}", e.getMessage(), e);
            subscription.setFetchStatus("failed");
            subscription.setErrorMessage(e.getMessage());
            subscriptionRepository.save(subscription);
            throw new RuntimeException("RSS采集失败: " + e.getMessage());
        }

        result.put("subscription_id", subscriptionId);
        result.put("fetched_count", fetchedCount);
        result.put("new_articles", newCount);
        result.put("fetch_time", LocalDateTime.now());
        result.put("articles", newArticlesList);

        return result;
    }

    /**
     * 从RSS条目创建文章实体
     */
    private RssArticle createArticleFromEntry(SyndEntry entry, RssSubscription subscription, List<String> categories) {
        RssArticle article = new RssArticle();

        article.setSubscriptionId(subscription.getId());
        article.setSubscriptionName(subscription.getName());
        article.setTitle(entry.getTitle());
        article.setLink(entry.getLink());
        article.setGuid(entry.getUri());
        article.setAuthor(entry.getAuthor());
        article.setCategories(categories);
        article.setLanguage(subscription.getLanguage());
        article.setContentType("rss");

        // 处理描述内容
        String description = "";
        if (entry.getDescription() != null) {
            description = entry.getDescription().getValue();
        } else if (!entry.getContents().isEmpty()) {
            description = entry.getContents().get(0).getValue();
        }
        article.setDescription(description);

        // 提取纯文本内容
        String contentText = Jsoup.parse(description).text();
        article.setContentText(contentText);
        article.setWordCount(contentText.length());

        // 生成摘要
        String summary = contentText.length() > 200 ? contentText.substring(0, 200) + "..." : contentText;
        article.setSummary(summary);

        // 处理发布时间
        Date pubDate = entry.getPublishedDate();
        if (pubDate == null) {
            pubDate = entry.getUpdatedDate();
        }
        if (pubDate == null) {
            pubDate = new Date();
        }
        article.setPubDate(pubDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        // 设置时间戳
        LocalDateTime now = LocalDateTime.now();
        article.setCreatedAt(now);
        article.setUpdatedAt(now);

        // 计算重要度评分（简单算法）
        double importanceScore = calculateImportanceScore(article);
        article.setImportanceScore(importanceScore);

        return article;
    }

    /**
     * 提取分类信息
     */
    private List<String> extractCategories(SyndEntry entry) {
        List<String> categories = new ArrayList<>();
        if (entry.getCategories() != null) {
            for (SyndCategory category : entry.getCategories()) {
                String name = category.getName();
                if (name != null && !name.trim().isEmpty()) {
                    categories.add(name.trim());
                }
            }
        }
        return categories;
    }

    /**
     * 更新订阅源信息
     */
    private void updateSubscriptionFromFeed(RssSubscription subscription, SyndFeed feed) {
        subscription.setChannelTitle(feed.getTitle());
        subscription.setChannelLink(feed.getLink());
        subscription.setChannelDescription(feed.getDescription());
        subscription.setLanguage(feed.getLanguage());
        subscription.setGenerator(feed.getGenerator());
        subscription.setWebMaster(feed.getWebMaster());

        if (feed.getImage() != null) {
            RssSubscription.RssImageInfo imageInfo = new RssSubscription.RssImageInfo();
            imageInfo.setUrl(feed.getImage().getUrl());
            imageInfo.setTitle(feed.getImage().getTitle());
            imageInfo.setLink(feed.getImage().getLink());
            subscription.setImage(imageInfo);
        }

        subscription.setLastFetchedAt(LocalDateTime.now());
        subscription.setFetchStatus("success");
        subscription.setErrorMessage(null);
        subscription.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 更新订阅源信息（从另一个订阅源）
     */
    private void updateSubscriptionFromFeed(RssSubscription target, RssSubscription source) {
        target.setName(source.getName());
        target.setUrl(source.getUrl());
        target.setChannelTitle(source.getChannelTitle());
        target.setChannelLink(source.getChannelLink());
        target.setChannelDescription(source.getChannelDescription());
        target.setLanguage(source.getLanguage());
        target.setGenerator(source.getGenerator());
        target.setWebMaster(source.getWebMaster());
        target.setImage(source.getImage());
        target.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 计算文章重要度评分
     */
    private double calculateImportanceScore(RssArticle article) {
        double score = 0.0;

        // 基于字数的评分
        if (article.getWordCount() != null) {
            score += Math.min(article.getWordCount() / 1000.0, 2.0);
        }

        // 基于分类数量的评分
        if (article.getCategories() != null) {
            score += article.getCategories().size() * 0.1;
        }

        // 基于标题长度的评分
        if (article.getTitle() != null) {
            score += Math.min(article.getTitle().length() / 100.0, 1.0);
        }

        return Math.round(score * 100.0) / 100.0;
    }

    @Override
    @Transactional
    public RssSubscription saveOrUpdateSubscription(RssSubscription subscription) {
        Optional<RssSubscription> existing = subscriptionRepository.findByUrl(subscription.getUrl());

        if (existing.isPresent()) {
            RssSubscription existingSub = existing.get();
            // 更新现有订阅源的信息
            updateSubscriptionFromFeed(existingSub, subscription);
            return subscriptionRepository.save(existingSub);
        } else {
            // 创建新订阅源
            subscription.setCreatedAt(LocalDateTime.now());
            subscription.setUpdatedAt(LocalDateTime.now());
            subscription.setEnabled(true);
            subscription.setFetchStatus("pending");
            return subscriptionRepository.save(subscription);
        }
    }

    @Override
    public RssSubscription findSubscriptionByUrl(String url) {
        return subscriptionRepository.findByUrl(url).orElse(null);
    }

    @Override
    public List<RssSubscription> getAllEnabledSubscriptions() {
        return subscriptionRepository.findByEnabledTrue();
    }

    @Override
    public List<RssArticle> getArticlesByCategory(String category) {
        return articleRepository.findByCategories(category);
    }

    @Override
    public List<RssArticle> getArticlesBySubscription(String subscriptionId) {
        return articleRepository.findBySubscriptionId(subscriptionId);
    }

    @Override
    public List<RssArticle> searchArticles(String keyword) {
        return articleRepository.findByContentContaining(keyword);
    }

    @Override
    public List<RssCategory> getHotCategories(int limit) {
        return categoryRepository.findTop10ByOrderByArticleCountDesc()
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateCategoryStatistics(List<String> categories) {
        Set<String> uniqueCategories = new HashSet<>(categories);

        for (String categoryName : uniqueCategories) {
            Optional<RssCategory> existing = categoryRepository.findByName(categoryName);

            if (existing.isPresent()) {
                RssCategory category = existing.get();
                category.setArticleCount((int) articleRepository.countByCategories(categoryName));
                category.setLastUsedAt(LocalDateTime.now());
                category.setUpdatedAt(LocalDateTime.now());
                categoryRepository.save(category);
            } else {
                RssCategory category = new RssCategory();
                category.setName(categoryName);
                category.setArticleCount((int) articleRepository.countByCategories(categoryName));
                category.setCreatedAt(LocalDateTime.now());
                category.setUpdatedAt(LocalDateTime.now());
                category.setLastUsedAt(LocalDateTime.now());
                category.setEnabled(true);
                categoryRepository.save(category);
            }
        }
    }

    @Override
    @Transactional
    public long cleanupOldArticles(LocalDateTime beforeDate) {
        List<RssArticle> oldArticles = articleRepository.findByPubDateBefore(beforeDate);
        long count = oldArticles.size();
        articleRepository.deleteAll(oldArticles);
        log.info("清理了 {} 篇过期文章", count);
        return count;
    }

    @Override
    public Map<String, Object> getArticleStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("total_articles", articleRepository.count());
        stats.put("total_subscriptions", subscriptionRepository.count());
        stats.put("enabled_subscriptions", subscriptionRepository.countByEnabledTrue());
        stats.put("total_categories", categoryRepository.count());
        stats.put("enabled_categories", categoryRepository.countByEnabledTrue());

        // 最近7天的文章统计
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        List<RssArticle> recentArticles = articleRepository.findByPubDateBetween(weekAgo, LocalDateTime.now());
        stats.put("articles_last_week", recentArticles.size());

        return stats;
    }
}
