package com.zhouyi.service;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.HeadlineDetailDTO;
import com.zhouyi.dto.HeadlinePublishDTO;
import com.zhouyi.dto.HeadlineQueryDTO;
import com.zhouyi.dto.HeadlineUpdateDTO;

import java.util.Map;

/**
 * 新闻头条服务接口
 */
public interface HeadlineService {

    /**
     * 分页查询头条列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Result<Map<String, Object>> getHeadlinesByPage(HeadlineQueryDTO queryDTO);

    /**
     * 根据ID查询头条详情
     * 
     * @param hid 头条ID
     * @return 头条详情
     */
    Result<HeadlineDetailDTO> getHeadlineById(Integer hid);

    /**
     * 发布头条
     * 
     * @param publishDTO 发布信息
     * @param publisher  发布者ID
     * @return 发布结果
     */
    Result<String> publishHeadline(HeadlinePublishDTO publishDTO, Integer publisher);

    /**
     * 修改头条
     * 
     * @param updateDTO 更新信息
     * @param userId    当前用户ID
     * @return 更新结果
     */
    Result<String> updateHeadline(HeadlineUpdateDTO updateDTO, Integer userId);

    /**
     * 删除头条
     *
     * @param hid    头条ID
     * @param userId 当前用户ID
     * @return 删除结果
     */
    Result<String> deleteHeadline(Integer hid, Integer userId);

    /**
     * 更新新闻状态（审核）
     * 
     * @param hid    头条ID
     * @param status 状态 (1: 通过, 2: 拒绝)
     * @param userId 审核人ID
     * @return 处理结果
     */
    Result<String> updateHeadlineStatus(Integer hid, Integer status, Integer userId);
}
