package com.zhouyi.mapper;

import com.zhouyi.entity.SystemConfig;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 系统配置数据访问层
 */
@Mapper
public interface SystemConfigMapper {

    /**
     * 根据配置键查询配置
     */
    @Select("SELECT * FROM system_config WHERE config_key = #{configKey}")
    SystemConfig selectByConfigKey(@Param("configKey") String configKey);

    /**
     * 根据配置类型查询配置列表
     */
    @Select("SELECT * FROM system_config WHERE config_type = #{configType} ORDER BY id")
    List<SystemConfig> selectByConfigType(@Param("configType") String configType);

    /**
     * 查询所有配置
     */
    @Select("SELECT * FROM system_config ORDER BY config_type, id")
    List<SystemConfig> selectAll();

    /**
     * 插入配置
     */
    @Insert("INSERT INTO system_config(config_key, config_value, config_type, description, is_system, created_time, updated_time) " +
            "VALUES(#{configKey}, #{configValue}, #{configType}, #{description}, #{isSystem}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SystemConfig systemConfig);

    /**
     * 更新配置
     */
    @Update("UPDATE system_config SET config_value = #{configValue}, description = #{description}, " +
            "updated_time = NOW() WHERE config_key = #{configKey}")
    int updateByConfigKey(@Param("configKey") String configKey, 
                         @Param("configValue") String configValue, 
                         @Param("description") String description);

    /**
     * 更新配置实体
     */
    @Update("UPDATE system_config SET config_value = #{configValue}, config_type = #{configType}, " +
            "description = #{description}, updated_time = NOW() WHERE id = #{id}")
    int updateById(SystemConfig systemConfig);

    /**
     * 删除配置
     */
    @Delete("DELETE FROM system_config WHERE config_key = #{configKey}")
    int deleteByConfigKey(@Param("configKey") String configKey);

    /**
     * 批量更新配置
     */
    @Update("<script>" +
            "UPDATE system_config SET config_value = " +
            "<foreach collection='configs' item='config' index='key' separator=' '>" +
            "CASE WHEN config_key = #{key} THEN #{config} ELSE config_value END " +
            "</foreach>" +
            ", updated_time = NOW() WHERE config_key IN " +
            "<foreach collection='configs' item='config' index='key' open='(' separator=',' close=')'>" +
            "#{key}" +
            "</foreach>" +
            "</script>")
    int batchUpdate(@Param("configs") java.util.Map<String, String> configs);
}
