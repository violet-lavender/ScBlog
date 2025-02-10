package com.wyz.blog.pojo.domain;

import lombok.Data;

/**
 * 博客用户概述
 */
@Data
public class BlogUserGeneral {

	/**
	 * 用户id
	 */
	Integer userId;

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
