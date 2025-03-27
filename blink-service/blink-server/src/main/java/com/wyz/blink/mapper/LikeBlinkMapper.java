package com.wyz.blink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wyz.blink.pojo.LikeBlink;
import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

/**
 * 动态点赞状态Mapper
 */
@Mapper
public interface LikeBlinkMapper extends BaseMapper<LikeBlink> {

    /**
     * 获取用户对某列表动态的点赞状态
     *
     * @param userId      用户id
     * @param blinkIdList 动态id列表
     * @return 在列表中点过赞的动态
     */
    Set<Integer> selectMapByUserIdAndBlinkIdList(Integer userId, Integer[] blinkIdList);

}
