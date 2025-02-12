package com.wyz.comment.pojo;

import lombok.Data;

import java.util.List;

/**
 * 评论VO
 */
@Data
public class CommentVO {

	/**
	 * 父评论信息
	 */
	CommentBO info;

	/**
	 * 子评论信息列表
	 */
	List<CommentBO> sub;

	/**
	 * 子评论信息条数
	 */
	Long subCount;

}
