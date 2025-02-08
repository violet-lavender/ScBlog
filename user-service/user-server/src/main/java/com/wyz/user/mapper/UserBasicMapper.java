package com.wyz.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wyz.user.pojo.UserBasic;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户基本信息表 Mapper 接口
 */
@Mapper
public interface UserBasicMapper extends BaseMapper<UserBasic> {

}
