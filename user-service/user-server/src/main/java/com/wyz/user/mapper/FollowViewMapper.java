package com.wyz.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wyz.user.pojo.FollowView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 关注视图 Mapper 接口
 */
@Mapper
public interface FollowViewMapper extends BaseMapper<FollowView> {

	/**
	 * 获取用户关注作者的id列表
	 *
	 * @param userId 用户id
	 * @return 被用户关注的id列表
	 */
	@Select("select follow_id from user.follow_view where user_id = #{userId} and status = 1;")
	List<Integer> selectFollowIdByUserId(int userId);

}
