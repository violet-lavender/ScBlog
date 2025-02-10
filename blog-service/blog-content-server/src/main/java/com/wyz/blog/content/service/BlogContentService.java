package com.wyz.blog.content.service;

import com.wyz.blog.content.pojo.BlogListVO;
import com.wyz.blog.content.pojo.SearchQuery;

/**
 * @author 阿杆
 * @version 1.0
 * @date 2022/7/8 17:39
 */
public interface BlogContentService {

	/**
	 * 搜索博客
	 *
	 * @param searchQuery 搜索条件
	 * @return 搜索到的结果
	 */
	BlogListVO searchBlog(SearchQuery searchQuery);

}
