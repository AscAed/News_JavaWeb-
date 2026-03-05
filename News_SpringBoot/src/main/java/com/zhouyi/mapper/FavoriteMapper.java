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
        @Insert("INSERT INTO favorites (user_id, hid, created_time) " +
                "VALUES (#{userId}, #{newsId}, #{createdTime})")
        @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
        int insert(Favorite favorite);

        /**
         * 根据ID查找收藏记录
         */
        @Select("SELECT id, user_id, hid AS newsId, favorite_time AS createdTime " +
                "FROM favorites WHERE id = #{id}")
        Favorite findById(Integer id);

        /**
         * 根据用户ID和新闻ID查找收藏记录
         */
        @Select("SELECT id, user_id, hid AS newsId, favorite_time AS createdTime " +
                "FROM favorites WHERE user_id = #{userId} AND hid = #{newsId}")
        Favorite findByUserIdAndNewsId(@Param("userId") Integer userId, @Param("newsId") Integer newsId);

        /**
         * 根据用户ID查找收藏列表
         */
        @Select("SELECT id, user_id, hid AS newsId, favorite_time AS createdTime " +
                "FROM favorites WHERE user_id = #{userId} ORDER BY favorite_time DESC")
        List<Favorite> findByUserId(@Param("userId") Integer userId);

        /**
         * 分页查询用户收藏列表
         */
        @Select("SELECT id, user_id, hid AS newsId, favorite_time AS createdTime " +
                "FROM favorites WHERE user_id = #{userId} " +
                "ORDER BY favorite_time DESC LIMIT #{offset}, #{pageSize}")
        List<Favorite> findByUserIdWithPage(@Param("userId") Integer userId,
                        @Param("offset") Integer offset,
                        @Param("pageSize") Integer pageSize);

        /**
         * 统计用户收藏数量
         */
        @Select("SELECT COUNT(*) FROM favorites WHERE user_id = #{userId}")
        long countByUserId(@Param("userId") Integer userId);

        /**
         * 统计新闻收藏数量
         */
        @Select("SELECT COUNT(*) FROM favorites WHERE hid = #{newsId}")
        long countByNewsId(@Param("newsId") Integer newsId);

        /**
         * NOTE: 'note' field and 'updated_time' field do not exist in 'favorites'
         * table.
         * 更新收藏备注 (We will leave this empty or remove if not needed,
         * but since the interface exists, I'll provide an empty or best-effort update)
         */
        @Update("UPDATE favorites SET favorite_time = #{updatedTime} " +
                "WHERE id = #{id}")
        int updateNote(Favorite favorite);

        /**
         * 删除收藏记录
         */
        @Delete("DELETE FROM favorites WHERE id = #{id}")
        int deleteById(Integer id);

        /**
         * 根据用户ID和新闻ID删除收藏记录
         */
        @Delete("DELETE FROM favorites WHERE user_id = #{userId} AND hid = #{newsId}")
        int deleteByUserIdAndNewsId(@Param("userId") Integer userId, @Param("newsId") Integer newsId);

        /**
         * 批量插入收藏记录
         */
        @Insert({
                        "<script>",
                "INSERT INTO favorites (user_id, hid, favorite_time) VALUES ",
                        "<foreach collection='favorites' item='favorite' separator=','>",
                "(#{favorite.userId}, #{favorite.newsId}, #{favorite.createdTime})",
                        "</foreach>",
                        "</script>"
        })
        int batchInsert(@Param("favorites") List<Favorite> favorites);

        /**
         * 批量删除收藏记录（根据用户ID和新闻ID列表）
         */
        @Delete({
                        "<script>",
                "DELETE FROM favorites WHERE user_id = #{userId} AND hid IN ",
                        "<foreach collection='newsIds' item='newsId' open='(' separator=',' close=')'>",
                        "#{newsId}",
                        "</foreach>",
                        "</script>"
        })
        int batchDeleteByUserIdAndNewsIds(@Param("userId") Integer userId, @Param("newsIds") List<Integer> newsIds);
}
