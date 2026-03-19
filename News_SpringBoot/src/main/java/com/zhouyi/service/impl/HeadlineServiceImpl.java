package com.zhouyi.service.impl;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.HeadlineDetailDTO;
import com.zhouyi.dto.HeadlinePublishDTO;
import com.zhouyi.dto.HeadlineQueryDTO;
import com.zhouyi.dto.HeadlineUpdateDTO;
import com.zhouyi.entity.Headline;
import com.zhouyi.entity.mongodb.NewsContent;
import com.zhouyi.mapper.HeadlineMapper;
import com.zhouyi.service.HeadlineService;
import com.zhouyi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zhouyi.entity.elasticsearch.HeadlineEsEntity;
import com.zhouyi.repository.elasticsearch.HeadlineEsRepository;
import com.zhouyi.service.NewsSearchService;
import com.zhouyi.dto.SearchResultDTO;
import com.zhouyi.dto.UserProfileDTO;
import lombok.extern.slf4j.Slf4j;
import java.util.stream.Collectors;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 新闻头条服务实现类
 */
@Service
@Slf4j
public class HeadlineServiceImpl implements HeadlineService {

    @Autowired
    private HeadlineMapper headlineMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private com.zhouyi.service.HybridRssService hybridRssService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private HeadlineEsRepository headlineEsRepository;

    @Autowired
    private NewsSearchService newsSearchService;

    @Override
    @SuppressWarnings("unchecked")
    public Result<Map<String, Object>> getHeadlinesByPage(HeadlineQueryDTO queryDTO) {
        try {
            // First Page Fast Cache (Hot News List)
            String cacheKey = "headlines:page:1:" + queryDTO.getType() + ":" + queryDTO.getSourceType();
            if (queryDTO.getPageNum() == 1 && (queryDTO.getKeywords() == null || queryDTO.getKeywords().isEmpty())) {
                Object cachedResult = redisTemplate.opsForValue().get(cacheKey);
                if (cachedResult != null) {
                    return Result.successWithMessageAndData("查询成功 (from Cache)", (Map<String, Object>) cachedResult);
                }
            }

            // Check if this is a request for RSS Zaobao section
            if ("rss".equalsIgnoreCase(queryDTO.getSourceType()) && queryDTO.getSourceId() != null
                    && queryDTO.getSection() != null) {
                try {
                    Long subId = Long.valueOf(queryDTO.getSourceId());
                    // Synchronously wait for the fetch to complete with a 10s timeout
                    hybridRssService.fetchAndSave(subId, queryDTO.getSection()).get(10,
                            java.util.concurrent.TimeUnit.SECONDS);
                } catch (Exception e) {
                    // Log but don't fail the whole request if fetch times out
                    System.err.println("Failed to fetch RSS synchronously: " + e.getMessage());
                }
            }

            // 1. Check if we should use Elasticsearch for keyword search
            if (queryDTO.getKeywords() != null && !queryDTO.getKeywords().trim().isEmpty()) {
                SearchResultDTO searchResult = newsSearchService.globalSearch(
                        queryDTO.getKeywords(), queryDTO.getType(), queryDTO.getPageNum(), queryDTO.getPageSize());

                List<HeadlineEsEntity> items = searchResult.getItems();
                if (items == null || items.isEmpty()) {
                    Map<String, Object> emptyResult = new HashMap<>();
                    emptyResult.put("total", 0L);
                    emptyResult.put("page", queryDTO.getPageNum());
                    emptyResult.put("page_size", queryDTO.getPageSize());
                    emptyResult.put("total_pages", 0);
                    emptyResult.put("items", List.of());
                    return Result.successWithMessageAndData("查询成功 (ES empty)", emptyResult);
                }

                // 2. Fetch full Headline data from MySQL
                List<Integer> ids = items.stream()
                        .map(HeadlineEsEntity::getHid)
                        .collect(Collectors.toList());
                
                // Fetch only published news to be safe
                List<Headline> dbHeadlines = headlineMapper.selectHeadlinesByIds(ids);
                
                // 3. Map to preserve order and overlay highlights
                Map<Integer, Headline> dbMap = dbHeadlines.stream()
                        .filter(h -> h.getStatus() == 1) // Ensure only published news are shown
                        .collect(Collectors.toMap(Headline::getHid, h -> h));
                
                List<Headline> highlightedHeadlines = items.stream()
                        .map(esItem -> {
                            Headline h = dbMap.get(esItem.getHid());
                            if (h != null) {
                                // Overlay highlighting for title
                                if (esItem.getTitle() != null && esItem.getTitle().contains("<em")) {
                                    h.setTitle(esItem.getTitle());
                                }
                                
                                // Overlay highlighting for summary/article snippet
                                if (esItem.getArticle() != null && esItem.getArticle().contains("<em")) {
                                    h.setSummary(esItem.getArticle());
                                } else if (h.getSummary() == null || h.getSummary().isEmpty()) {
                                    // If no highlight but summary is empty, use a snippet of the article
                                    String article = esItem.getArticle();
                                    if (article != null) {
                                        h.setSummary(article.length() > 200 ? article.substring(0, 200) + "..." : article);
                                    }
                                }
                            }
                            return h;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                Map<String, Object> result = new HashMap<>();
                result.put("total", searchResult.getTotal());
                result.put("page", queryDTO.getPageNum());
                result.put("page_size", queryDTO.getPageSize());
                result.put("total_pages", (int) Math.ceil((double) searchResult.getTotal() / queryDTO.getPageSize()));
                result.put("items", highlightedHeadlines);

                return Result.successWithMessageAndData("查询成功 (from ES)", result);
            }

            // Calculation and MySQL Query (Fallback/Default)
            int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
            
            // 查询头条列表
            List<Headline> headlines = headlineMapper.selectHeadlinesByPage(
                    offset, queryDTO.getPageSize(), queryDTO.getType(),
                    queryDTO.getKeywords(), queryDTO.getPublisher(), queryDTO.getLang(),
                    queryDTO.getSourceType(), queryDTO.getSourceId(), queryDTO.getSection());

            // 查询总数
            Long total = headlineMapper.countHeadlines(
                    queryDTO.getType(), queryDTO.getKeywords(), queryDTO.getPublisher(), queryDTO.getLang(),
                    queryDTO.getSourceType(), queryDTO.getSourceId(), queryDTO.getSection());

            // 计算总页数
            int totalPages = (int) Math.ceil((double) total / queryDTO.getPageSize());

            // 构建返回结果，符合API文档规范
            Map<String, Object> result = new HashMap<>();
            result.put("total", total);
            result.put("page", queryDTO.getPageNum());
            result.put("page_size", queryDTO.getPageSize());
            result.put("total_pages", totalPages);
            result.put("items", headlines);

            // Save to cache for 5 minutes if it's the first page
            if (queryDTO.getPageNum() == 1 && (queryDTO.getKeywords() == null || queryDTO.getKeywords().isEmpty())) {
                redisTemplate.opsForValue().set(cacheKey, result, 5, TimeUnit.MINUTES);
            }

            return Result.successWithMessageAndData("查询成功", result);

        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "articleDetail", key = "#hid", sync = true)
    public Result<HeadlineDetailDTO> getHeadlineById(Integer hid) {
        try {
            // 查询MySQL中的头条基本信息
            Headline headline = headlineMapper.selectHeadlineById(hid);
            if (headline == null) {
                return Result.error("头条不存在");
            }

            // 增加浏览量
            headlineMapper.incrementPageViews(hid);
            headline.setPageViews(headline.getPageViews() + 1);

            // 查询MongoDB中的详细内容
            String docId = headline.getMongodbDocumentId();

            // 构建详情DTO
            HeadlineDetailDTO detailDTO = new HeadlineDetailDTO();
            detailDTO.setId(headline.getHid()); // API统一字段
            detailDTO.setTitle(headline.getTitle());
            detailDTO.setTypeId(headline.getType());
            detailDTO.setTypeName(headline.getTypeName());
            detailDTO.setPageViews(headline.getPageViews());
            detailDTO.setAuthorId(headline.getPublisher());
            detailDTO.setAuthor(headline.getAuthor());
            detailDTO.setSummary(headline.getSummary());
            detailDTO.setCoverImage(headline.getCoverImage());
            detailDTO.setStatus(headline.getStatus());
            detailDTO.setIsTop(headline.getIsTop() != null && headline.getIsTop() == 1);
            detailDTO.setCreatedTime(headline.getCreatedTime());
            detailDTO.setUpdatedTime(headline.getUpdatedTime());
            detailDTO.setPublishedTime(headline.getPublishedTime());

            boolean isRss = "rss".equals(headline.getSourceType())
                    || "rss_articles".equals(headline.getMongodbCollection());

            if (isRss) {
                com.zhouyi.entity.mongo.RssArticle rssArticle = null;
                if (docId != null && !docId.isEmpty()) {
                    rssArticle = mongoTemplate.findById(docId, com.zhouyi.entity.mongo.RssArticle.class);
                } else {
                    org.springframework.data.mongodb.core.query.Query q = new org.springframework.data.mongodb.core.query.Query(
                            org.springframework.data.mongodb.core.query.Criteria.where("mysql_headline_id").is(hid));
                    rssArticle = mongoTemplate.findOne(q, com.zhouyi.entity.mongo.RssArticle.class);
                }

                if (rssArticle != null) {
                    detailDTO.setContent(rssArticle.getContentText());
                    if (rssArticle.getWordCount() != null) {
                        detailDTO.setWordCount(rssArticle.getWordCount());
                    }
                    if (rssArticle.getCategories() != null && !rssArticle.getCategories().isEmpty()) {
                        detailDTO.setTags(rssArticle.getCategories());
                    } else if (headline.getTags() != null && !headline.getTags().isEmpty()) {
                        detailDTO.setTags(java.util.Arrays.asList(headline.getTags().split(",")));
                    }
                } else {
                    if (headline.getTags() != null && !headline.getTags().isEmpty()) {
                        detailDTO.setTags(java.util.Arrays.asList(headline.getTags().split(",")));
                    }
                }
            } else {
                NewsContent newsContent = null;
                if (docId != null && !docId.isEmpty()) {
                    newsContent = mongoTemplate.findById(docId, NewsContent.class);
                }
                if (newsContent == null) {
                    newsContent = mongoTemplate.findById(hid, NewsContent.class);
                }

                // 设置文章内容
                if (newsContent != null) {
                    detailDTO.setContent(newsContent.getContent());
                    // 如果MongoDB中有更完整的摘要，使用MongoDB的
                    if (newsContent.getSummary() != null && !newsContent.getSummary().isEmpty()) {
                        detailDTO.setSummary(newsContent.getSummary());
                    }
                    // 设置关键词
                    if (newsContent.getKeywords() != null) {
                        detailDTO.setKeywords(newsContent.getKeywords());
                    }
                    // 设置字数统计
                    if (newsContent.getWordCount() != null) {
                        detailDTO.setWordCount(newsContent.getWordCount());
                    }
                    // 设置阅读时间
                    if (newsContent.getReadingTime() != null) {
                        detailDTO.setReadingTime(newsContent.getReadingTime());
                    }
                    // 设置SEO信息
                    if (newsContent.getSeoInfo() != null) {
                        detailDTO.setSeoTitle(headline.getTitle()); // 使用新闻标题作为SEO标题
                        detailDTO.setSeoDescription(newsContent.getSeoInfo().getMetaDescription());
                        detailDTO.setSeoKeywords(newsContent.getSeoInfo().getMetaKeywords());
                    }
                    // 处理标签：将字符串转换为列表
                    if (newsContent.getTagList() != null && !newsContent.getTagList().isEmpty()) {
                        detailDTO.setTags(newsContent.getTagList());
                    } else if (headline.getTags() != null && !headline.getTags().isEmpty()) {
                        // 如果MongoDB中没有标签，从MySQL的字符串转换
                        String[] tagArray = headline.getTags().split(",");
                        detailDTO.setTags(java.util.Arrays.asList(tagArray));
                    }
                } else {
                    // 如果MongoDB中没有数据，从MySQL转换标签
                    if (headline.getTags() != null && !headline.getTags().isEmpty()) {
                        String[] tagArray = headline.getTags().split(",");
                        detailDTO.setTags(java.util.Arrays.asList(tagArray));
                    }
                }
            }

            // 设置统计数据（从实体获取）
            detailDTO.setLikeCount(headline.getLikeCount() != null ? headline.getLikeCount() : 0);
            detailDTO.setCommentCount(headline.getCommentCount() != null ? headline.getCommentCount() : 0);
            detailDTO.setPageViews(headline.getPageViews() != null ? headline.getPageViews() : 0);
            detailDTO.setShareCount(0); // 分享数暂未实现

            // 设置作者头像
            detailDTO.setAuthorAvatar("https://example.com/avatars/default.jpg"); // 默认头像
            if (headline.getPublisher() != null) {
                try {
                    Result<UserProfileDTO> userProfile = userService.getUserProfile(headline.getPublisher());
                    if (userProfile.getCode() == 200 && userProfile.getData() != null) {
                        detailDTO.setAuthorAvatar(userProfile.getData().getAvatar());
                    }
                } catch (Exception e) {
                    log.error("Failed to fetch author avatar for user {}: {}", headline.getPublisher(), e.getMessage());
                }
            }

            return Result.successWithMessageAndData("查询成功", detailDTO);

        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<String> publishHeadline(HeadlinePublishDTO publishDTO, Integer publisher) {
        try {
            // 获取发布者信息（使用JWT验证的用户ID）
            var profileResult = userService.getUserProfile(publisher);
            if (profileResult.getCode() != 200 || profileResult.getData() == null) {
                return Result.error("发布者不存在");
            }
            UserProfileDTO profile = profileResult.getData();

            // 创建头条实体
            Headline headline = new Headline();
            headline.setTitle(publishDTO.getTitle());
            headline.setType(publishDTO.getType());
            headline.setSummary(publishDTO.getSummary());
            headline.setCoverImage(publishDTO.getCoverImage());
            headline.setTags(publishDTO.getTags());
            headline.setPublisher(publisher); // 使用JWT验证的用户ID
            headline.setAuthor(profile.getUsername());

            // 只有媒体用户需要审核 (status=0)
            if ("media".equalsIgnoreCase(profile.getRole_name())) {
                headline.setStatus(0); // 审核中
            } else {
                headline.setStatus(1); // 已发布
            }

            headline.setIsTop(0); // 不置顶
            headline.setCreatedTime(LocalDateTime.now());
            headline.setUpdatedTime(LocalDateTime.now());
            headline.setPublishedTime(LocalDateTime.now());

            // 获取类型名称
            String typeName = headlineMapper.selectTypeNameByType(publishDTO.getType());
            headline.setTypeName(typeName != null ? typeName : "未知类型");

            // Set new source tracking fields - defaults for manual API creation
            headline.setSourceType(com.zhouyi.common.enums.NewsSource.API.getCode());
            headline.setMongodbCollection("news");

            // Language detection
            String lang = headline.getTitle() != null && headline.getTitle().matches(".*[\\u4e00-\\u9fa5].*") ? "zh"
                    : "en";
            headline.setLang(lang);

            // 插入头条
            int rows = headlineMapper.insertHeadline(headline);
            if (rows <= 0) {
                return Result.error("发布失败");
            }

            // 保存详细内容到MongoDB
            NewsContent newsContent = new NewsContent();
            newsContent.setHid(headline.getHid());
            newsContent.setTitle(publishDTO.getTitle());
            newsContent.setContent(publishDTO.getArticle());
            newsContent.setSummary(publishDTO.getSummary());
            newsContent.setCoverImage(publishDTO.getCoverImage());
            newsContent.setAuthor(profile.getUsername());

            // Set source fields in MongoDB
            newsContent.setSourceType(com.zhouyi.common.enums.NewsSource.API);
            newsContent.setMysqlHeadlineId(headline.getHid());

            // 处理标签：将字符串转换为列表
            if (publishDTO.getTags() != null && !publishDTO.getTags().isEmpty()) {
                List<String> tagList = Arrays.asList(publishDTO.getTags().split(","));
                newsContent.setTagList(tagList);
            }

            newsContent.setCreatedTime(LocalDateTime.now());
            newsContent.setUpdatedTime(LocalDateTime.now());

            NewsContent savedContent = mongoTemplate.save(newsContent);

            // Update Headline with MongoDB Document ID
            headline.setMongodbDocumentId(savedContent.getId());
            headlineMapper.updateHeadline(headline);

            // 同步到 Elasticsearch
            try {
                HeadlineEsEntity esEntity = new HeadlineEsEntity();
                esEntity.setHid(headline.getHid());
                esEntity.setTitle(headline.getTitle());
                esEntity.setArticle(publishDTO.getArticle());
                esEntity.setTypeName(headline.getTypeName());
                esEntity.setType(headline.getType());
                esEntity.setPageViews(headline.getPageViews());
                headlineEsRepository.save(esEntity);
            } catch (Exception e) {
                // ES同步失败不应影响主业务逻辑，仅记录日志
                System.err.println("Elasticsearch sync failed: " + e.getMessage());
            }

            return Result.success("发布成功");

        } catch (Exception e) {
            return Result.error("发布失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "articleDetail", key = "#updateDTO.id != null ? #updateDTO.id : #updateDTO.hid")
    public Result<String> updateHeadline(HeadlineUpdateDTO updateDTO, Integer userId) {
        try {
            // 检查头条是否存在
            Headline existingHeadline = headlineMapper
                    .selectHeadlineById(updateDTO.getId() != null ? updateDTO.getId() : updateDTO.getHid());
            if (existingHeadline == null) {
                return Result.error("头条不存在");
            }

            // 验证权限: 只有原作者才能修改
            if (!existingHeadline.getPublisher().equals(userId)) {
                return Result.error("无权修改该新闻,只有作者才能修改自己的作品");
            }

            // 更新头条基本信息
            Headline headline = new Headline();
            headline.setHid(updateDTO.getId() != null ? updateDTO.getId() : updateDTO.getHid());
            headline.setTitle(updateDTO.getTitle());
            headline.setType(updateDTO.getType());
            headline.setSummary(updateDTO.getSummary());
            headline.setCoverImage(updateDTO.getCoverImage());
            headline.setTags(updateDTO.getTags());

            // 获取类型名称
            String typeName = headlineMapper.selectTypeNameByType(updateDTO.getType());
            headline.setTypeName(typeName != null ? typeName : "未知类型");

            int rows = headlineMapper.updateHeadline(headline);
            if (rows <= 0) {
                return Result.error("更新失败");
            }

            // 更新MongoDB中的详细内容
            NewsContent newsContent = mongoTemplate.findById(
                    updateDTO.getId() != null ? updateDTO.getId() : updateDTO.getHid(), NewsContent.class);
            if (newsContent != null) {
                newsContent.setTitle(updateDTO.getTitle());
                newsContent.setContent(updateDTO.getArticle());
                newsContent.setSummary(updateDTO.getSummary());
                newsContent.setCoverImage(updateDTO.getCoverImage());

                // 处理标签:将字符串转换为列表
                if (updateDTO.getTags() != null && !updateDTO.getTags().isEmpty()) {
                    List<String> tagList = Arrays.asList(updateDTO.getTags().split(","));
                    newsContent.setTagList(tagList);
                }

                newsContent.setUpdatedTime(LocalDateTime.now());
                mongoTemplate.save(newsContent);
            }

            // 同步到 Elasticsearch
            try {
                HeadlineEsEntity esEntity = new HeadlineEsEntity();
                esEntity.setHid(headline.getHid());
                esEntity.setTitle(headline.getTitle());
                esEntity.setArticle(updateDTO.getArticle());
                esEntity.setTypeName(headline.getTypeName());
                esEntity.setType(headline.getType());
                esEntity.setPageViews(existingHeadline.getPageViews()); // 使用原有的浏览量
                headlineEsRepository.save(esEntity);
            } catch (Exception e) {
                System.err.println("Elasticsearch sync failed (update): " + e.getMessage());
            }

            return Result.success("更新成功");

        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "articleDetail", key = "#hid")
    public Result<String> deleteHeadline(Integer hid, Integer userId) {
        try {
            // 检查头条是否存在
            Headline headline = headlineMapper.selectHeadlineById(hid);
            if (headline == null) {
                return Result.error("头条不存在");
            }

            // 验证权限: 只有原作者才能删除
            if (!headline.getPublisher().equals(userId)) {
                return Result.error("无权删除该新闻,只有作者才能删除自己的作品");
            }

            // 删除MySQL中的头条
            int rows = headlineMapper.deleteHeadline(hid);
            if (rows <= 0) {
                return Result.error("删除失败");
            }

            // 删除MongoDB中的详细内容
            NewsContent newsContent = mongoTemplate.findById(hid, NewsContent.class);
            if (newsContent != null) {
                mongoTemplate.remove(newsContent);
            }

            // 从 Elasticsearch 中删除
            try {
                headlineEsRepository.deleteById(hid);
            } catch (Exception e) {
                System.err.println("Elasticsearch sync failed (delete): " + e.getMessage());
            }

            return Result.success("删除成功");

        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<String> updateHeadlineStatus(Integer hid, Integer status, Integer adminId) {
        try {
            // 验证管理员权限（由Controller的PreAuthorize保证，这里简单记录）
            log.info("Admin {} is updating headline {} status to {}", adminId, hid, status);

            // 检查头条是否存在
            Headline headline = headlineMapper.selectHeadlineById(hid);
            if (headline == null) {
                return Result.error("新闻不存在");
            }

            // 更新数据库
            int rows = headlineMapper.updateStatus(hid, status);
            if (rows <= 0) {
                return Result.error("更新状态失败");
            }

            // 如果审核通过，同步到 ES
            if (status == 1) {
                try {
                    // 获取完整内容
                    Result<HeadlineDetailDTO> contentResult = getHeadlineById(hid);
                    if (contentResult.getCode() == 200 && contentResult.getData() != null) {
                        HeadlineDetailDTO detail = contentResult.getData();
                        HeadlineEsEntity esEntity = new HeadlineEsEntity();
                        esEntity.setHid(headline.getHid());
                        esEntity.setTitle(headline.getTitle());
                        esEntity.setArticle(detail.getContent());
                        esEntity.setTypeName(headline.getTypeName());
                        esEntity.setType(headline.getType());
                        esEntity.setPageViews(headline.getPageViews());
                        headlineEsRepository.save(esEntity);
                        log.info("Headline {} published and synced to ES", hid);
                    }
                } catch (Exception e) {
                    log.error("Sync to ES failed after approval: {}", e.getMessage());
                }
            } else if (status == 2) {
                // 如果下线/拒绝，从 ES 删除
                try {
                    headlineEsRepository.deleteById(hid);
                    log.info("Headline {} offlined and removed from ES", hid);
                } catch (Exception e) {
                    log.error("Remove from ES failed after offline: {}", e.getMessage());
                }
            }

            return Result.success("状态更新成功");
        } catch (Exception e) {
            log.error("Update headline status failed", e);
            return Result.error("更新失败：" + e.getMessage());
        }
    }
}
