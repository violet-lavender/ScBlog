package com.wyz.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyz.blog.mapper.BlogViewMapper;
import com.wyz.blog.mapper.ViewBlogMapper;
import com.wyz.blog.pojo.domain.BlogView;
import com.wyz.blog.pojo.domain.ViewBlog;
import com.wyz.blog.pojo.vo.BlogListVO;
import com.wyz.blog.service.ViewBlogService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 博客浏览记录服务实现类
 */
@Slf4j
@Service
public class ViewBlogServiceImpl extends ServiceImpl<ViewBlogMapper, ViewBlog> implements ViewBlogService {

    @Resource
    private ViewBlogMapper viewBlogMapper;

    @Resource
    private BlogViewMapper blogViewMapper;

    @Override
    public BlogListVO getViewBlogList(@Valid @NotNull Integer userId, int page, int pageSize) {
        // 查询用户的浏览记录, 按时间倒序获取浏览的博客id
        LambdaQueryWrapper<ViewBlog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ViewBlog::getUserId, userId).orderByDesc(ViewBlog::getCreateTime);
        IPage<ViewBlog> iPage = new Page<>(page, pageSize);
        viewBlogMapper.selectPage(iPage, wrapper);
        BlogListVO blogListVO = BeanUtil.copyProperties(iPage, BlogListVO.class);
        List<ViewBlog> records = iPage.getRecords();
        if (records.isEmpty()) {
            return blogListVO;
        }
        ArrayList<Integer> blogIdList = new ArrayList<>();
        for (ViewBlog record : records) {
            blogIdList.add(record.getBlogId());
        }
        LambdaQueryWrapper<BlogView> blogWrapper = new LambdaQueryWrapper<>();
        blogWrapper.in(BlogView::getId, blogIdList);
        List<BlogView> blogViewList = blogViewMapper.selectList(blogWrapper);

        blogListVO.setRecords(blogViewList);
        return blogListVO;
    }

    @Override
    public Boolean clearViewBlogList(Integer userId) {
        int affectRows = viewBlogMapper.clearViewBlogs(userId);
        return affectRows > 0;
    }
}
