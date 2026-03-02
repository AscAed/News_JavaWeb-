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
    @Insert("INSERT INTO news_types (tname, description, icon_url, sort_order, status, created_time, updated_time, source_type, source_id) "
            +
            "VALUES (#{typeName}, #{description}, #{iconUrl}, #{sortOrder}, #{status}, #{createdTime}, #{updatedTime}, #{sourceType}, #{sourceId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(NewsType newsType);

    /**
     * 根据ID查找分类记录
     */
    @Select("SELECT tid as id, tname as typeName, description, icon_url as iconUrl, " +
            "sort_order as sortOrder, status, created_time as createdTime, updated_time as updatedTime, " +
            "source_type as sourceType, source_id as sourceId " +
            "FROM news_types WHERE tid = #{id}")
    NewsType findById(Integer id);

    /**
     * 根据名称查找分类记录
     */
    @Select("SELECT tid as id, tname as typeName, description, icon_url as iconUrl, " +
            "sort_order as sortOrder, status, created_time as createdTime, updated_time as updatedTime, " +
            "source_type as sourceType, source_id as sourceId " +
            "FROM news_types WHERE tname = #{name}")
    NewsType findByName(String name);

    /**
     * 获取所有分类列表
     */
    @Select("SELECT tid as id, tname as typeName, description, icon_url as iconUrl, " +
            "sort_order as sortOrder, status, created_time as createdTime, updated_time as updatedTime, " +
            "source_type as sourceType, source_id as sourceId " +
            "FROM news_types ORDER BY sort_order ASC, created_time DESC")
    List<NewsType> findAll();

    /**
     * 根据状态获取分类列表
     */
    @Select("<script>" +
            "SELECT tid as id, tname as typeName, description, icon_url as iconUrl, " +
            "sort_order as sortOrder, status, created_time as createdTime, updated_time as updatedTime, " +
            "source_type as sourceType, source_id as sourceId " +
            "FROM news_types WHERE status = #{status} " +
            "<if test='sourceType != null and sourceType != \"\"'> AND source_type = #{sourceType} </if> " +
            "<if test='sourceId != null and sourceId != \"\"'> AND source_id = #{sourceId} </if> " +
            "ORDER BY sort_order ASC, created_time DESC" +
            "</script>")
    List<NewsType> findByStatus(@Param("status") Integer status, @Param("sourceType") String sourceType,
                                @Param("sourceId") String sourceId);

    /**
     * 分页查询分类列表
     */
    @Select("SELECT tid as id, tname as typeName, description, icon_url as iconUrl, " +
            "sort_order as sortOrder, status, created_time as createdTime, updated_time as updatedTime, " +
            "source_type as sourceType, source_id as sourceId " +
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
    @Select("<script>" +
            "SELECT COUNT(*) FROM news_types WHERE status = #{status} " +
            "<if test='sourceType != null and sourceType != \"\"'> AND source_type = #{sourceType} </if> " +
            "<if test='sourceId != null and sourceId != \"\"'> AND source_id = #{sourceId} </if>" +
            "</script>")
    long countByStatus(@Param("status") Integer status, @Param("sourceType") String sourceType,
                       @Param("sourceId") String sourceId);

    /**
     * 更新分类信息
     */
    @Update("UPDATE news_types SET tname = #{typeName}, description = #{description}, " +
            "icon_url = #{iconUrl}, sort_order = #{sortOrder}, updated_time = #{updatedTime}, " +
            "source_type = #{sourceType}, source_id = #{sourceId} " +
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
