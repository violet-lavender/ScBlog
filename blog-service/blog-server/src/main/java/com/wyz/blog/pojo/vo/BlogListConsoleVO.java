package com.wyz.blog.pojo.vo;

import com.wyz.blog.pojo.bo.BlogCountBO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 博客列表-控制台展示对象(加各状态及计数)
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BlogListConsoleVO extends com.wyz.blog.pojo.vo.BlogListVO {

	/**
	 * 各状态博客数量
	 */
	BlogCountBO count;

}
