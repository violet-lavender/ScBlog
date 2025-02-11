package com.wyz.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wyz.blog.pojo.domain.LikeBlog;
import com.wyz.blog.pojo.vo.BlogListVO;

/**
 * 博客点赞服务
 */
public interface LikeBlogService extends IService<LikeBlog> {

	/**
	 * 点赞博客，若点赞不存在，则添加，若已存在，则取消点赞
	 *
	 * @param userId 用户id
	 * @param blogId 博客id
	 * @return 点赞状态（false意为未点赞，即取消点赞
	 */
	boolean likeBlog(Integer userId, Integer blogId);

	/**
	 * 获取博客的点赞数量
	 *
	 * @param blogId 博客id
	 * @return 数量
	 */
	Long getLikeNum(Integer blogId);

	/**
	 * 获取用户点赞的博客列表
	 *
	 * @param userId   用户id
	 * @param page     第几页
	 * @param pageSize 页大小
	 * @return 点赞博客列表
	 */
	BlogListVO getLikeBlogList(Integer userId, int page, int pageSize);

}
