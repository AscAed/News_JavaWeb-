package com.zhouyi.mapper;

import com.zhouyi.entity.NewsType;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 新闻分类数据访问层
 */
@Mapper
public interface NewsTypeMapper {
    
    /**
     * 插入分类记录
     */
    @Insert("INSERT INTO news_types (tname, description, icon_url, sort_order, status, created_time, updated_time) " +
            "VALUES (#{typeName}, #{description}, #{iconUrl}, #{sortOrder}, #{status}, #{createdTime}, #{updatedTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(NewsType newsType);
    
    /**
     * 根据ID查找分类记录
     */
    @Select("SELECT tid as id, tname as typeName, description, icon_url as iconUrl, " +
            "sort_order as sortOrder, status, created_time as createdTime, updated_time as updatedTime " +
            "FROM news_types WHERE tid = #{id}")
    NewsType findById(Integer id);
    
    /**
     * 根据名称查找分类记录
     */
    @Select("SELECT tid as id, tname as typeName, description, icon_url as iconUrl, " +
            "sort_order as sortOrder, status, created_time as createdTime, updated_time as updatedTime " +
            "FROM news_types WHERE tname = #{name}")
    NewsType findByName(String name);
    
    /**
     * 获取所有分类列表
     */
    @Select("SELECT tid as id, tname as typeName, description, icon_url as iconUrl, " +
            "sort_order as sortOrder, status, created_time as createdTime, updated_time as updatedTime " +
            "FROM news_types ORDER BY sort_order ASC, created_time DESC")
    List<NewsType> findAll();
    
    /**
     * 根据状态获取分类列表
     */
    @Select("SELECT tid as id, tname as typeName, description, icon_url as iconUrl, " +
            "sort_order as sortOrder, status, created_time as createdTime, updated_time as updatedTime " +
            "FROM news_types WHERE status = #{status} ORDER BY sort_order ASC, created_time DESC")
    List<NewsType> findByStatus(Integer status);
    
    /**
     * 分页查询分类列表
     */
    @Select("SELECT tid as id, tname as typeName, description, icon_url as iconUrl, " +
            "sort_order as sortOrder, status, created_time as createdTime, updated_time as updatedTime " +
            "FROM news_types WHERE status = #{status} " +
            "ORDER BY ${sortBy} ${sortOrder} LIMIT #{offset}, #{pageSize}")
    List<NewsType> findByPage(@Param("status") Integer status, 
                             @Param("sortBy") String sortBy, 
                             @Param("sortOrder") String sortOrder,
                             @Param("offset") Integer offset, 
                             @Param("pageSize") Integer pageSize);
    
    /**
     * 统计分类总数
     */
    @Select("SELECT COUNT(*) FROM news_types WHERE status = #{status}")
    long countByStatus(@Param("status") Integer status);
    
    /**
     * 更新分类信息
     */
    @Update("UPDATE news_types SET tname = #{typeName}, description = #{description}, " +
            "icon_url = #{iconUrl}, sort_order = #{sortOrder}, updated_time = #{updatedTime} " +
            "WHERE tid = #{id}")
    int update(NewsType newsType);
    
    /**
     * 更新分类状态
     */
    @Update("UPDATE news_types SET status = #{status}, updated_time = #{updatedTime} " +
            "WHERE tid = #{id}")
    int updateStatus(@Param("id") Integer id, 
                    @Param("status") Integer status, 
                    @Param("updatedTime") java.time.LocalDateTime updatedTime);
    
    /**
     * 软删除分类记录
     */
    @Update("UPDATE news_types SET status = 0, updated_time = #{updatedTime} WHERE tid = #{id}")
    int softDelete(@Param("id") Integer id, 
                   @Param("updatedTime") java.time.LocalDateTime updatedTime);
    
    /**
     * 硬删除分类记录
     */
    @Delete("DELETE FROM news_types WHERE tid = #{id}")
    int deleteById(Integer id);
    
    /**
     * 获取下一个排序值
     */
    @Select("SELECT COALESCE(MAX(sort_order), 0) + 1 FROM news_types")
    Integer getNextSortOrder();
    
    /**
     * 统计分类下的新闻数量
     */
    @Select("SELECT COUNT(*) FROM headlines WHERE type = #{type} AND status = 1")
    long countNewsByType(@Param("type") Integer type);
}
