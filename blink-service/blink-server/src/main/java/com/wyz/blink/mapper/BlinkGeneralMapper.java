package com.wyz.blink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wyz.blink.pojo.BlinkGeneral;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * Blink概述 Mapper
 */
@Mapper
public interface BlinkGeneralMapper extends BaseMapper<BlinkGeneral> {

    /**
     * 动态点赞数+1
     *
     * @param blinkId 动态id
     */
    @Update("update blink_general set likes_num = likes_num + 1, score = score + 3 where blink_id = #{blinkId};")
    void increaseLikeNum(Integer blinkId);

    /**
     * 动态点赞数-1
     *
     * @param blinkId 动态id
     */
    @Update("update blink_general set likes_num = likes_num - 1, score = score - 3 where blink_id = #{blinkId} and likes_num > 0;")
    void decreaseLikeNum(Integer blinkId);

}
