package com.wyz.blog.pojo.vo;

import com.wyz.blog.pojo.bo.BlogStatusBO;
import com.wyz.blog.pojo.domain.BlogContentHtml;
import com.wyz.user.dto.UserDTO;
import lombok.Data;

/**
 * 博客内容VO(视图对象)
 */
@Data
public class BlogContentVO {

	/**
	 * 博客基本信息,带用户状态
	 */
	BlogStatusBO info;

	/**
	 * 博客内容信息
	 */
	BlogContentHtml content;

	/**
	 * 作者信息
	 */
	UserDTO author;

}
