package com.wyz.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wyz.blog.pojo.domain.BlogUserGeneral;
import com.wyz.blog.pojo.domain.BlogView;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 博客视图Mapper
 */
@Mapper
public interface BlogViewMapper extends BaseMapper<BlogView> {

	/**
	 * 查询用户发送的博客各项数据的统计
	 *
	 * @param userIds 用户id集合
	 * @return 统计数据
	 */
	List<BlogUserGeneral> selectBlogViewsByUserIds(Integer[] userIds);

}
