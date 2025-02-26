package com.wyz.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wyz.user.pojo.UserBasic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.io.Serializable;

/**
 * 用户基本信息表 Mapper 接口
 */
@Mapper
public interface UserBasicMapper extends BaseMapper<UserBasic> {

    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     * @return 影响的行数
     */
    @Override
    @Update("update `user_basic` set `deleted` = #{id} where `user_id` = #{id} and `deleted` = 0")
    int deleteById(Serializable id);

}
