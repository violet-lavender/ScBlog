package com.wyz.blog.pojo.bo;

import com.wyz.blog.pojo.domain.BlogView;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 博客详情信息业务对象, 视图加昵称
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BlogInfoBO extends BlogView {

	/**
	 * 作者昵称
	 */
	String authorName;

}
