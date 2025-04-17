package com.wyz.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wyz.blog.pojo.domain.Blog;
import com.wyz.blog.pojo.domain.BlogUserGeneral;
import com.wyz.blog.pojo.vo.BlogContentVO;
import com.wyz.blog.pojo.vo.BlogDraftVO;
import com.wyz.blog.pojo.vo.BlogStatusListVO;
import com.wyz.blog.service.BlogService;
import com.wyz.blog.service.BlogViewService;
import com.wyz.blog.type.BlogStatusType;
import com.wyz.common.web.auth.AuthHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 获取博客信息的相关接口
 */
@Slf4j
@RestController
@RequestMapping("/blog")
public class BlogController {

	private final int pageSize = 20;
	@Resource
	private BlogService blogService;
	@Resource
	private BlogViewService blogViewService;

	/**
	 * 获取推荐博客列表
	 *
	 * @return 博客列表
	 */
	@GetMapping("/list/recommend")
	public BlogStatusListVO getRecommendBlog(@RequestParam(defaultValue = "1") int page) {
		log.debug("searchBlog,page->{}", page);
		Integer id = AuthHelper.getCurrentUserId();
		return blogViewService.getRecommendBlogList(id, page, pageSize);
	}

	/**
	 * 获取最新博客列表
	 */
	@GetMapping("/list/new")
	public BlogStatusListVO getNewBlog(@RequestParam(defaultValue = "1") int page) {
		log.debug("searchBlog,page->{}", page);
		Integer id = AuthHelper.getCurrentUserId();
		return blogViewService.getNewBlogList(id, page, pageSize);
	}

	/**
	 * 获取关注用户发表的博客列表，这个必须得登录才行
	 */
	@GetMapping("/list/follow")
	public BlogStatusListVO getFollowBlog(@RequestParam(defaultValue = "1") int page) {
		log.debug("searchBlog,page->{}", page);
		Integer id = AuthHelper.getCurrentUserIdOrExit();
		return blogViewService.getFollowBlogList(id, page, pageSize);
	}

	/**
	 * 获取草稿博客详情
	 * 这里获取的是 content 而不是 html, 因为要在编辑器中展示，继续编辑草稿
	 *
	 * @param id 草稿博客id
	 */
	@GetMapping("/draft")
	public BlogDraftVO getDraftBlog(@RequestParam Integer id) {
		Integer userId = AuthHelper.getCurrentUserIdOrExit();
		return blogService.getDraftBlog(id, userId);
	}

	/**
	 * 获取博客内容
	 *
	 * @param id 博客id
	 */
	@GetMapping("/detail")
	public BlogContentVO getBlogContentVO(@RequestParam Integer id) {
		Integer userId = AuthHelper.getCurrentUserId();
		return blogViewService.getBlogContentHtml(id, userId);
	}

	/**
	 * 获取博客基本信息，适合后端内部调用，不合适前端使用
	 *
	 * @param id 博客id
	 */
	@GetMapping("/blog")
	public Blog getBlog(@RequestParam Integer id) {
		LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(Blog::getId, id).eq(Blog::getStatus, BlogStatusType.PUBLISH.getValue());
		return blogService.getOne(wrapper);
	}

	/**
	 * 获取用户所发表的博客各项数据
	 *
	 * @param userIds 用户id
	 * @return 用户发表博客数据统计
	 */
	@GetMapping("/general")
	public List<BlogUserGeneral> getBlogUserGeneral(@RequestParam Integer[] userIds) {
		return blogService.getUserBlogGeneral(userIds);
	}

}
