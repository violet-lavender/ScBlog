package com.wyz.blog.pojo.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 博客状态信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BlogStatusBO extends BlogInfoBO {

	/**
	 * 点赞收藏等状态
	 */
	ActionStatusBO actionStatus;

}
