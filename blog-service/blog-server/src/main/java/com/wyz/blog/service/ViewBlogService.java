package com.wyz.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wyz.blog.pojo.domain.ViewBlog;
import com.wyz.blog.pojo.vo.BlogListVO;

/**
 * 博客浏览服务
 */
public interface ViewBlogService extends IService<ViewBlog> {

    /**
     * 获取用户浏览的博客列表
     *
     * @param userId   用户id
     * @param page     第几页
     * @param pageSize 页大小
     * @return 浏览博客列表
     */
    BlogListVO getViewBlogList(Integer userId, int page, int pageSize);

    /**
     * 清除用户浏览博客列表
     *
     * @param userId
     * @return
     */
    Boolean clearViewBlogList(Integer userId);
}
