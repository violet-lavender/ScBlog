package com.wyz.blog.content.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 带有作者昵称的博客文档对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BlogDocAndAuthorName extends BlogDoc {

	/**
	 * 作者昵称
	 */
	String authorName;

}
