package com.zhouyi.mapper;

import com.zhouyi.entity.OperationLog;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 操作日志数据访问层
 */
@Mapper
public interface OperationLogMapper {

    /**
     * 插入操作日志
     */
    @Insert("INSERT INTO operation_logs(user_id, username, operation_type, resource_type, resource_id, " +
            "description, ip_address, user_agent, created_at) " +
            "VALUES(#{userId}, #{username}, #{operationType}, #{resourceType}, #{resourceId}, " +
            "#{description}, #{ipAddress}, #{userAgent}, #{createdTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OperationLog operationLog);

    /**
     * 分页查询操作日志
     */
    @Select("<script><![CDATA[" +
            "SELECT * FROM operation_logs WHERE 1=1 " +
            "<if test='userId != null'> AND user_id = #{userId} </if>" +
            "<if test=\"action != null and action != ''\"> AND operation_type = #{action} </if>" +
            "<if test=\"dateFrom != null and dateFrom != ''\"> AND DATE(created_at) >= #{dateFrom} </if>" +
            "<if test=\"dateTo != null and dateTo != ''\"> AND DATE(created_at) <= #{dateTo} </if>" +
            "ORDER BY created_at DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "]]></script>")
    List<OperationLog> selectByPage(@Param("userId") Integer userId,
                                   @Param("action") String action,
                                   @Param("dateFrom") String dateFrom,
                                   @Param("dateTo") String dateTo,
                                   @Param("offset") Integer offset,
                                   @Param("pageSize") Integer pageSize);

    /**
     * 统计操作日志总数
     */
    @Select("<script><![CDATA[" +
            "SELECT COUNT(*) FROM operation_logs WHERE 1=1 " +
            "<if test='userId != null'> AND user_id = #{userId} </if>" +
            "<if test=\"action != null and action != ''\"> AND operation_type = #{action} </if>" +
            "<if test=\"dateFrom != null and dateFrom != ''\"> AND DATE(created_at) >= #{dateFrom} </if>" +
            "<if test=\"dateTo != null and dateTo != ''\"> AND DATE(created_at) <= #{dateTo} </if>" +
            "]]></script>")
    Long countByCondition(@Param("userId") Integer userId,
                         @Param("action") String action,
                         @Param("dateFrom") String dateFrom,
                         @Param("dateTo") String dateTo);

    /**
     * 根据用户ID查询操作日志
     */
    @Select("SELECT * FROM operation_logs WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{limit}")
    List<OperationLog> selectByUserId(@Param("userId") Integer userId, @Param("limit") Integer limit);

    /**
     * 根据操作类型查询操作日志
     */
    @Select("SELECT * FROM operation_logs WHERE operation_type = #{operationType} ORDER BY created_at DESC LIMIT #{limit}")
    List<OperationLog> selectByOperationType(@Param("operationType") String operationType, @Param("limit") Integer limit);

    /**
     * 删除指定日期之前的日志
     */
    @Delete("DELETE FROM operation_logs WHERE created_at < #{date}")
    int deleteByDate(@Param("date") String date);

    /**
     * 统计各操作类型的数量
     */
    @Select("SELECT operation_type, COUNT(*) as count FROM operation_logs " +
            "WHERE created_at >= DATE_SUB(NOW(), INTERVAL #{days} DAY) " +
            "GROUP BY operation_type ORDER BY count DESC")
    List<java.util.Map<String, Object>> countByOperationType(@Param("days") Integer days);
}
