package com.zhouyi.service.impl;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.HeadlineDetailDTO;
import com.zhouyi.entity.Headline;
import com.zhouyi.entity.mongodb.NewsContent;
import com.zhouyi.mapper.HeadlineMapper;
import com.zhouyi.service.HeadlineService;
import com.zhouyi.service.UserService;
import com.zhouyi.dto.HeadlineQueryDTO;
import com.zhouyi.dto.HeadlinePublishDTO;
import com.zhouyi.dto.HeadlineUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public Result<Map<String, Object>> getHeadlinesByPage(HeadlineQueryDTO queryDTO) {
        try {
            // 计算偏移量
            int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();

            // 查询头条列表
            List<Headline> headlines = headlineMapper.selectHeadlinesByPage(
                    offset, queryDTO.getPageSize(), queryDTO.getType(),
                    queryDTO.getKeywords(), queryDTO.getPublisher());

            // 查询总数
            Long total = headlineMapper.countHeadlines(
                    queryDTO.getType(), queryDTO.getKeywords(), queryDTO.getPublisher());

            // 计算总页数
            int totalPages = (int) Math.ceil((double) total / queryDTO.getPageSize());

            // 构建返回结果，符合API文档规范
            Map<String, Object> result = new HashMap<>();
            result.put("total", total);
            result.put("page", queryDTO.getPageNum());
            result.put("page_size", queryDTO.getPageSize());
            result.put("total_pages", totalPages);
            result.put("items", headlines);

            return Result.successWithMessageAndData("查询成功", result);

        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @Override
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
            NewsContent newsContent = mongoTemplate.findById(hid, NewsContent.class);

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

            // 设置统计数据（默认值，实际应从统计表获取）
            detailDTO.setLikeCount(0);
            detailDTO.setCommentCount(0);
            detailDTO.setShareCount(0);

            // 设置作者头像（默认值，实际应从用户表获取）
            detailDTO.setAuthorAvatar("https://example.com/avatars/default.jpg");

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
            var userResult = userService.getUserById(publisher);
            if (userResult.getCode() != 200 || userResult.getData() == null) {
                return Result.error("发布者不存在");
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
            newsContent.setAuthor(userResult.getData().getUsername());

            // 处理标签：将字符串转换为列表
            if (publishDTO.getTags() != null && !publishDTO.getTags().isEmpty()) {
                List<String> tagList = Arrays.asList(publishDTO.getTags().split(","));
                newsContent.setTagList(tagList);
            }

            newsContent.setCreatedTime(LocalDateTime.now());
            newsContent.setUpdatedTime(LocalDateTime.now());

            mongoTemplate.save(newsContent);

            return Result.success("发布成功");

        } catch (Exception e) {
            return Result.error("发布失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<String> updateHeadline(HeadlineUpdateDTO updateDTO) {
        try {
            // 检查头条是否存在
            Headline existingHeadline = headlineMapper
                    .selectHeadlineById(updateDTO.getId() != null ? updateDTO.getId() : updateDTO.getHid());
            if (existingHeadline == null) {
                return Result.error("头条不存在");
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

                // 处理标签：将字符串转换为列表
                if (updateDTO.getTags() != null && !updateDTO.getTags().isEmpty()) {
                    List<String> tagList = Arrays.asList(updateDTO.getTags().split(","));
                    newsContent.setTagList(tagList);
                }

                newsContent.setUpdatedTime(LocalDateTime.now());
                mongoTemplate.save(newsContent);
            }

            return Result.success("更新成功");

        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<String> deleteHeadline(Integer hid) {
        try {
            // 检查头条是否存在
            Headline headline = headlineMapper.selectHeadlineById(hid);
            if (headline == null) {
                return Result.error("头条不存在");
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

            return Result.success("删除成功");

        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}
