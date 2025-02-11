package com.wyz.comment.sdk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 使用 rabbitMQ 传输用户评论操作的事件实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

	/**
	 * 被评论的博客id
	 */
	Integer blogId;

	/**
	 * 执行的用户id，可能为空
	 */
	Integer userId;

	/**
	 * 用户评论的内容
	 */
	String content;

	/**
	 * 博客作者id
	 */
	Integer authorId;

}
