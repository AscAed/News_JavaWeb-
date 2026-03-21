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
import com.zhouyi.service.OutboxService;
import com.zhouyi.service.NewsSearchService;
import com.zhouyi.dto.SearchResultDTO;
import java.util.stream.Collectors;
import com.zhouyi.common.result.ResultCode;
import com.zhouyi.common.exception.BusinessException;
import com.zhouyi.component.NewsMetricsService;

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
public class HeadlineServiceImpl implements HeadlineService {

    @Autowired
    private HeadlineMapper headlineMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private OutboxService outboxService;

    @Autowired
    private NewsSearchService newsSearchService;

    @Autowired
    private NewsMetricsService newsMetricsService;

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
            newsMetricsService.incrementArticleView();
            // 查询MySQL中的头条基本信息
            Headline headline = headlineMapper.selectHeadlineById(hid);
            if (headline == null) {
                return Result.error("头条不存在");
            }

            // 增加浏览量 (改为调用异步Redis方法，此处不再同步更新MySQL)
            // incrementViewCount(hid); // 在Controller调用更合适，以免缓存命中时失效

            // 查询细节并考虑Redis缓冲中的浏览量
            Integer redisViews = 0;
            Object viewBuffer = redisTemplate.opsForValue().get("headline:page_views:" + hid);
            if (viewBuffer != null) {
                redisViews = ((Number) viewBuffer).intValue();
            }
            headline.setPageViews(headline.getPageViews() + redisViews);

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

            // 设置统计数据（默认值，实际应从统计表获取）
            detailDTO.setLikeCount(0);
            detailDTO.setCommentCount(0);
            detailDTO.setShareCount(0);

            // 设置作者头像（默认值，实际应从用户表获取）
            detailDTO.setAuthorAvatar("https://example.com/avatars/default.jpg");

            return Result.successWithMessageAndData("查询成功", detailDTO);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ResultCode.SYSTEM_INNER_ERROR, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> publishHeadline(HeadlinePublishDTO publishDTO, Integer publisher) {
        String mongoDocumentId = null;
        try {
            // 获取发布者信息（使用JWT验证的用户ID）
            var userResult = userService.getUserById(publisher);
            if (userResult.getCode() != 200 || userResult.getData() == null) {
                throw new BusinessException(ResultCode.USER_NOT_EXIST);
            }

            // 创建头条实体
            Headline headline = new Headline();
            headline.setTitle(publishDTO.getTitle());
            headline.setType(publishDTO.getType());
            headline.setSummary(publishDTO.getSummary());
            headline.setCoverImage(publishDTO.getCoverImage());
            headline.setTags(publishDTO.getTags());
            headline.setPublisher(publisher); // 使用JWT验证的用户ID
            headline.setAuthor(userResult.getData().getUsername());
            headline.setStatus(1); // 已发布
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

            // 1. MySQL 插入元数据
            int rows = headlineMapper.insertHeadline(headline);
            if (rows <= 0) {
                throw new BusinessException(ResultCode.DATA_IS_WRONG);
            }

            // 2. MongoDB 保存富文本
            NewsContent newsContent = new NewsContent();
            newsContent.setHid(headline.getHid());
            newsContent.setTitle(publishDTO.getTitle());
            newsContent.setContent(publishDTO.getArticle());
            newsContent.setSummary(publishDTO.getSummary());
            newsContent.setCoverImage(publishDTO.getCoverImage());
            newsContent.setAuthor(userResult.getData().getUsername());

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
            mongoDocumentId = savedContent.getId(); // 记录已生成的 MongoDB ID

            // 3. MySQL 更新关联外键
            headline.setMongodbDocumentId(mongoDocumentId);
            headlineMapper.updateHeadline(headline);

            // 4. 发送异步事件同步到 Elasticsearch (改为 Outbox 模式)
            outboxService.saveEsSyncMessage(headline.getHid(), "SAVE");

            newsMetricsService.incrementHeadlinePublished();
            return Result.success("发布成功");

        } catch (Exception e) {
            // 导师画重点：记录日志并触发补偿与回滚
            System.err.println("新闻发布流转失败，触发数据清洗与回滚逻辑: " + e.getMessage());
            
            if (mongoDocumentId != null) {
                mongoTemplate.remove(new org.springframework.data.mongodb.core.query.Query(
                        org.springframework.data.mongodb.core.query.Criteria.where("_id").is(mongoDocumentId)), NewsContent.class);
            }
            
            try {
                org.springframework.transaction.interceptor.TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            } catch (Exception te) {
                // Ignore if no transaction
            }
            
            throw new BusinessException(ResultCode.DATA_IS_WRONG, e);
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
                throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
            }

            // 验证权限: 只有原作者才能修改
            if (!existingHeadline.getPublisher().equals(userId)) {
                throw new BusinessException(ResultCode.PERMISSION_NO_ACCESS);
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
                throw new BusinessException(ResultCode.DATA_IS_WRONG);
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

            // 发送异步事件同步到 Elasticsearch (改为 Outbox 模式)
            outboxService.saveEsSyncMessage(headline.getHid(), "SAVE");

            return Result.success("更新成功");

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ResultCode.DATA_IS_WRONG, e);
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
                throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
            }

            // 验证权限: 只有原作者才能删除
            if (!headline.getPublisher().equals(userId)) {
                throw new BusinessException(ResultCode.PERMISSION_NO_ACCESS);
            }

            // 删除MySQL中的头条
            int rows = headlineMapper.deleteHeadline(hid);
            if (rows <= 0) {
                throw new BusinessException(ResultCode.DATA_IS_WRONG);
            }

            // 删除MongoDB中的详细内容
            NewsContent newsContent = mongoTemplate.findById(hid, NewsContent.class);
            if (newsContent != null) {
                mongoTemplate.remove(newsContent);
            }

            // 通过异步事件从 Elasticsearch 中删除 (改为 Outbox 模式)
            outboxService.saveEsSyncMessage(hid, "DELETE");

            return Result.success("删除成功");

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ResultCode.DATA_IS_WRONG, e);
        }
    }

    @Override
    public void incrementViewCount(Integer hid) {
        if (hid == null) return;
        String key = "headline:page_views:" + hid;
        redisTemplate.opsForValue().increment(key);
    }
}
