package com.wyz.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wyz.user.pojo.UserView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户视图 Mapper 接口
 */
@Mapper
public interface UserViewMapper extends BaseMapper<UserView> {

    /**
     * 根据 id 获取用户视图
     *
     * @param id
     * @return 用户视图
     */
    @Select("select * from `user_view` where `id` = #{id}")
    UserView getById(Integer id);
}
