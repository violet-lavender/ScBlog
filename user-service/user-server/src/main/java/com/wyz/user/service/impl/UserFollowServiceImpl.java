package com.wyz.user.service.impl;

import cn.sticki.common.exception.BusinessException;
import com.wyz.user.mapper.FansViewMapper;
import com.wyz.user.mapper.FollowViewMapper;
import com.wyz.user.mapper.UserFollowMapper;
import com.wyz.user.pojo.*;
import com.wyz.user.service.UserFollowService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

/**
 * 用户关注服务实现类
 */
@Slf4j
@Service
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow> implements UserFollowService {

    @Resource
    private FollowViewMapper followViewMapper;

    @Resource
    private FansViewMapper fansViewMapper;

    /**
     * 关注用户，已关注的状态下则取消关注
     *
     * @param userId   用户id（操作者）
     * @param followId 关注者的id
     * @return 返回调用后是否为关注状态，若为关注则返回true、未关注则返回false
     */
    @Override
    public boolean follow(int userId, int followId) {
        if (userId == followId) {
            throw new BusinessException("不能关注自己");
        }
        // 关注用户，查询是否存在关注记录，若不存在，则添加记录，若存在，则取消记录
        UserFollow follow = lambdaQuery().eq(UserFollow::getFansId, userId).eq(UserFollow::getFollowId, followId).one();
        // 用户关注使用状态区分，用户快速点击的时候会存在insert多条数据的情况。
        if (Objects.nonNull(follow)) {
            // 用户的关注使用状态区分，把关注状态取反
            boolean isFollowed = follow.getStatus() == 1;
            lambdaUpdate().set(UserFollow::getStatus, isFollowed ? 0 : 1)
                    .eq(UserFollow::getId, follow.getId()).update();
            return !isFollowed;
        } else {
            UserFollow userFollow = new UserFollow();
            userFollow.setFansId(userId);
            userFollow.setFollowId(followId);
            userFollow.setStatus(1);
            userFollow.setCreateTime(new Timestamp(System.currentTimeMillis()));
            // 不存在，添加记录，返回true
            this.save(userFollow);
            return true;
        }
    }

    /**
     * 获取关注列表
     *
     * @param userId   用户od
     * @param page     第几页
     * @param pageSize 页大小
     * @return 关注列表
     */
    @Override
    public FollowViewListVO getFollowList(int userId, int page, int pageSize) {
        // 获取关注列表
        LambdaQueryWrapper<FollowView> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FollowView::getUserId, userId).eq(FollowView::getStatus, 1);
        IPage<FollowView> iPage = new Page<>(page, pageSize);
        followViewMapper.selectPage(iPage, wrapper);
        // 赋值返回
        FollowViewListVO listVO = new FollowViewListVO();
        BeanUtils.copyProperties(iPage, listVO);
        return listVO;
    }

    /**
     * 获取粉丝列表
     *
     * @param userId   用户od
     * @param page     第几页
     * @param pageSize 页大小
     * @return 粉丝列表
     */
    @Override
    public FansViewListVO getFansList(int userId, int page, int pageSize) {
        // 获取粉丝列表
        LambdaQueryWrapper<FansView> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FansView::getUserId, userId).eq(FansView::getStatus, 1);
        IPage<FansView> iPage = new Page<>(page, pageSize);
        fansViewMapper.selectPage(iPage, wrapper);
        FansViewListVO listVO = new FansViewListVO();
        BeanUtils.copyProperties(iPage, listVO);
        return listVO;
    }

    /**
     * 获取关注列表id
     *
     * @param userId 用户id
     * @return id列表
     */
    @Override
    public List<Integer> getFollowIdList(int userId) {
        return followViewMapper.selectFollowIdByUserId(userId);
    }

}
