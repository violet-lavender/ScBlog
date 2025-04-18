package com.wyz.blog.dto;

import lombok.Data;

/**
 * 博客概览dt
 */
@Data
public class BlogGeneralDTO {

	/**
	 * 用户id
	 */
	Integer userId;

	/**
	 * 博客id
	 */
	Integer blogId;

	/**
	 * 浏览量
	 */
	Integer viewNum;

	/**
	 * 点赞量
	 */
	Integer likeNum;

	/**
	 * 评论量
	 */
	Integer commentNum;

	/**
	 * 收藏量
	 */
	Integer collectionNum;

}
