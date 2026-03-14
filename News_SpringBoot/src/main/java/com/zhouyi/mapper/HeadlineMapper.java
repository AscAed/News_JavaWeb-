package com.zhouyi.mapper;

import com.zhouyi.entity.Headline;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 新闻头条Mapper接口
 */
@Mapper
public interface HeadlineMapper {

    /**
     * 分页查询头条列表
     *
     * @param pageNum   页码
     * @param pageSize  每页条数
     * @param type      新闻类型
     * @param keyWords  关键词
     * @param publisher 发布者ID
     * @param lang      语言分类
     * @return 头条列表
     */
    List<Headline> selectHeadlinesByPage(@Param("pageNum") Integer pageNum,
                                         @Param("pageSize") Integer pageSize,
                                         @Param("type") Integer type,
                                         @Param("keyWords") String keyWords,
                                         @Param("publisher") Integer publisher,
                                         @Param("lang") String lang,
                                         @Param("sourceType") String sourceType,
                                         @Param("sourceId") String sourceId,
                                         @Param("section") String section);

    /**
     * 统计头条总数
     *
     * @param type      新闻类型
     * @param keyWords  关键词
     * @param publisher 发布者ID
     * @param lang      语言分类
     * @return 总数
     */
    Long countHeadlines(@Param("type") Integer type,
                        @Param("keyWords") String keyWords,
                        @Param("publisher") Integer publisher,
                        @Param("lang") String lang,
                        @Param("sourceType") String sourceType,
                        @Param("sourceId") String sourceId,
                        @Param("section") String section);

    /**
     * 根据ID查询头条详情
     * 
     * @param hid 头条ID
     * @return 头条信息
     */
    Headline selectHeadlineById(@Param("hid") Integer hid);

    /**
     * 插入头条
     * 
     * @param headline 头条信息
     * @return 影响行数
     */
    int insertHeadline(Headline headline);

    /**
     * 更新头条
     * 
     * @param headline 头条信息
     * @return 影响行数
     */
    int updateHeadline(Headline headline);

    /**
     * 删除头条
     * 
     * @param hid 头条ID
     * @return 影响行数
     */
    int deleteHeadline(@Param("hid") Integer hid);

    /**
     * 增加浏览量
     * 
     * @param hid 头条ID
     * @return 影响行数
     */
    int incrementPageViews(@Param("hid") Integer hid);

    /**
     * 根据类型查询类型名称
     * 
     * @param type 类型ID
     * @return 类型名称
     */
    String selectTypeNameByType(@Param("type") Integer type);

    /**
     * 根据ID集合查询头条列表
     * 
     * @param hids ID列表
     * @return 头条列表
     */
    List<Headline> selectHeadlinesByIds(@Param("hids") List<Integer> hids);

    /**
     * 根据ID查询头条
     * 
     * @param id 头条ID
     * @return 头条信息
     */
    Headline selectById(@Param("id") Integer id);
}
