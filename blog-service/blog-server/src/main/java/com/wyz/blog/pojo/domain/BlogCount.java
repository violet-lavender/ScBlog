package com.wyz.blog.pojo.domain;

import lombok.Data;

/**
 * 博客计数
 */
@Data
public class BlogCount {

	/**
	 * 状态码
	 */
	Integer status;

	/**
	 * 当前状态对应的数量
	 */
	Integer number;

}
