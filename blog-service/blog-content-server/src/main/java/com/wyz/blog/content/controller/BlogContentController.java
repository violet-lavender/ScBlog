package com.wyz.blog.content.controller;

import com.wyz.blog.content.pojo.BlogListVO;
import com.wyz.blog.content.pojo.SearchQuery;
import com.wyz.blog.content.service.BlogContentService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 博客内容Controller
 */
@RestController
@RequestMapping("/blog/content")
public class BlogContentController {

	@Resource
	private BlogContentService blogContentService;

	/**
	 * 搜索博客
	 *
	 * @return 博客列表
	 */
	@GetMapping("/search")
	public BlogListVO searchBlog(@Validated SearchQuery searchQuery) {
		return blogContentService.searchBlog(searchQuery);
	}

}
