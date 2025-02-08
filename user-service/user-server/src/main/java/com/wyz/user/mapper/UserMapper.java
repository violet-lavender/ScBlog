package com.wyz.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wyz.user.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import java.io.Serializable;

/**
 * 用户 Mapper 接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

	/**
	 * 根据 ID 删除
	 *
	 * @param id 主键ID
	 * @return 影响数据行
	 */
	@Override
	int deleteById(Serializable id);

}
