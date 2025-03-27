package com.wyz.blink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyz.blink.mapper.BlinkGeneralMapper;
import com.wyz.blink.mapper.BlinkMapper;
import com.wyz.blink.mapper.LikeBlinkMapper;
import com.wyz.blink.pojo.Blink;
import com.wyz.blink.pojo.LikeBlink;
import com.wyz.blink.service.LikeBlinkService;
import com.wyz.common.exception.BusinessException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * 动态点赞服务实现类
 */
@Slf4j
@Service
public class LikeBlinkServiceImpl extends ServiceImpl<LikeBlinkMapper, LikeBlink> implements LikeBlinkService {

    @Resource
    private BlinkGeneralMapper blinkGeneralMapper;

    @Resource
    private LikeBlinkMapper likeBlinkMapper;

    @Resource
    private BlinkMapper blinkMapper;

    @Override
    public boolean likeBlink(Integer userId, Integer blinkId) {
        // 进行判断动态是否存在
        Blink blink = blinkMapper.selectById(blinkId);
        if (blink == null) {
            throw new BusinessException("当前动态不存在");
        }
        // 查询是否已经点赞，若已点赞则取消点赞
        LambdaQueryWrapper<LikeBlink> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikeBlink::getUserId, userId);
        wrapper.eq(LikeBlink::getBlinkId, blinkId);
        LikeBlink selectOne = likeBlinkMapper.selectOne(wrapper);
        if (selectOne != null) {
            // 点赞已经存在
            likeBlinkMapper.deleteById(selectOne);
            blinkGeneralMapper.decreaseLikeNum(blinkId);
            return false;
        } else {
            // 点赞不存在
            LikeBlink linkBlink = new LikeBlink();
            linkBlink.setBlinkId(blinkId);
            linkBlink.setUserId(userId);
            linkBlink.setCreateTime(new Timestamp(System.currentTimeMillis()));
            likeBlinkMapper.insert(linkBlink);
            blinkGeneralMapper.increaseLikeNum(blinkId);
            return true;
        }
    }

    @Override
    public Long getLikeNum(Integer blinkId) {
        LambdaQueryWrapper<LikeBlink> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikeBlink::getBlinkId, blinkId);
        return likeBlinkMapper.selectCount(wrapper);
    }
}
