package com.wyz.blog.pojo.domain;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 博客视图对象
 */
@Data
public class BlogView {

	/**
	 * 博客id
	 */
	Integer id;

	/**
	 * 作者id
	 */
	Integer authorId;

	/**
	 * 标题
	 */
	String title;

	/**
	 * 描述
	 */
	String description;

	/**
	 * 院校代码
	 */
	Integer schoolCode;

	/**
	 * 封面图
	 */
	String coverImage;

	/**
	 * 创建时间
	 */
	Timestamp createTime;

	/**
	 * 发表时间
	 */
	Timestamp releaseTime;

	/**
	 * 修改时间
	 */
	Timestamp modifiedTime;

	/**
	 * 发表状态（1表示已发表、2表示未发表、3为仅自己可见、4为回收站、5为审核中）
	 */
	Integer status;

	/**
	 * 博客创作类型：1. 原创; 2. 转载
	 */
	Integer writeType;

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

	/**
     * 评分
     */
    Double score;

}
