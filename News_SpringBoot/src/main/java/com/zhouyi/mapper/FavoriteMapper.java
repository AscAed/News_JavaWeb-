package com.zhouyi.mapper;

import com.zhouyi.entity.Favorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 收藏数据访问层
 */
@Mapper
public interface FavoriteMapper {

        /**
         * 插入收藏记录
         */
        @Insert("INSERT INTO news_favorites (user_id, headline_id, note, created_time, updated_time) " +
                        "VALUES (#{userId}, #{newsId}, #{note}, #{createdTime}, #{updatedTime})")
        @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "fid")
        int insert(Favorite favorite);

        /**
         * 根据ID查找收藏记录
         */
        @Select("SELECT fid AS id, user_id, headline_id AS newsId, note, created_time, updated_time " +
                        "FROM news_favorites WHERE fid = #{id}")
        Favorite findById(Integer id);

        /**
         * 根据用户ID和新闻ID查找收藏记录
         */
        @Select("SELECT fid AS id, user_id, headline_id AS newsId, note, created_time, updated_time " +
                        "FROM news_favorites WHERE user_id = #{userId} AND headline_id = #{newsId}")
        Favorite findByUserIdAndNewsId(@Param("userId") Integer userId, @Param("newsId") Integer newsId);

        /**
         * 根据用户ID查找收藏列表
         */
        @Select("SELECT fid AS id, user_id, headline_id AS newsId, note, created_time, updated_time " +
                        "FROM news_favorites WHERE user_id = #{userId} ORDER BY created_time DESC")
        List<Favorite> findByUserId(@Param("userId") Integer userId);

        /**
         * 分页查询用户收藏列表
         */
        @Select("SELECT fid AS id, user_id, headline_id AS newsId, note, created_time, updated_time " +
                        "FROM news_favorites WHERE user_id = #{userId} " +
                        "ORDER BY created_time DESC LIMIT #{offset}, #{pageSize}")
        List<Favorite> findByUserIdWithPage(@Param("userId") Integer userId,
                        @Param("offset") Integer offset,
                        @Param("pageSize") Integer pageSize);

        /**
         * 统计用户收藏数量
         */
        @Select("SELECT COUNT(*) FROM news_favorites WHERE user_id = #{userId}")
        long countByUserId(@Param("userId") Integer userId);

        /**
         * 统计新闻收藏数量
         */
        @Select("SELECT COUNT(*) FROM news_favorites WHERE headline_id = #{newsId}")
        long countByNewsId(@Param("newsId") Integer newsId);

        /**
         * 更新收藏备注
         */
        @Update("UPDATE news_favorites SET note = #{note}, updated_time = #{updatedTime} " +
                        "WHERE fid = #{id}")
        int updateNote(Favorite favorite);

        /**
         * 删除收藏记录
         */
        @Delete("DELETE FROM news_favorites WHERE fid = #{id}")
        int deleteById(Integer id);

        /**
         * 根据用户ID和新闻ID删除收藏记录
         */
        @Delete("DELETE FROM news_favorites WHERE user_id = #{userId} AND headline_id = #{newsId}")
        int deleteByUserIdAndNewsId(@Param("userId") Integer userId, @Param("newsId") Integer newsId);

        /**
         * 批量插入收藏记录
         */
        @Insert({
                        "<script>",
                        "INSERT INTO news_favorites (user_id, headline_id, note, created_time, updated_time) VALUES ",
                        "<foreach collection='favorites' item='favorite' separator=','>",
                        "(#{favorite.userId}, #{favorite.newsId}, #{favorite.note}, #{favorite.createdTime}, #{favorite.updatedTime})",
                        "</foreach>",
                        "</script>"
        })
        int batchInsert(@Param("favorites") List<Favorite> favorites);

        /**
         * 批量删除收藏记录（根据用户ID和新闻ID列表）
         */
        @Delete({
                        "<script>",
                        "DELETE FROM news_favorites WHERE user_id = #{userId} AND headline_id IN ",
                        "<foreach collection='newsIds' item='newsId' open='(' separator=',' close=')'>",
                        "#{newsId}",
                        "</foreach>",
                        "</script>"
        })
        int batchDeleteByUserIdAndNewsIds(@Param("userId") Integer userId, @Param("newsIds") List<Integer> newsIds);
}
