package com.wyz.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wyz.user.pojo.UserFollow;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户关注 Mapper 接口
 */
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {

}
