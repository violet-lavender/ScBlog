package com.wyz.blink.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wyz.blink.pojo.LikeBlink;

/**
 * 动态点赞服务
 */
public interface LikeBlinkService extends IService<LikeBlink> {
    /**
     * 点赞动态，若点赞不存在，则添加，若已存在，则取消点赞
     *
     * @param userId  用户id
     * @param blinkId 动态id
     * @return 点赞状态（false意为未点赞，即取消点赞
     */
    boolean likeBlink(Integer userId, Integer blinkId);

    /**
     * 获取动态的点赞数量
     *
     * @param blinkId 动态id
     * @return 数量
     */
    Long getLikeNum(Integer blinkId);
}
