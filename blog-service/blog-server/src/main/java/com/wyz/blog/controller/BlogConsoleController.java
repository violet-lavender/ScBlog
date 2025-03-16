package com.wyz.blog.controller;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wyz.blog.pojo.bo.BlogSaveBO;
import com.wyz.blog.pojo.domain.BlogContent;
import com.wyz.blog.pojo.domain.BlogView;
import com.wyz.blog.pojo.vo.BlogListConsoleVO;
import com.wyz.blog.pojo.vo.BlogStatisticsDataVO;
import com.wyz.blog.service.BlogService;
import com.wyz.blog.service.BlogViewService;
import com.wyz.blog.type.BlogStatusType;
import com.wyz.common.exception.BusinessException;
import com.wyz.common.result.RestResult;
import com.wyz.common.web.auth.AuthHelper;
import com.wyz.resource.type.FileType;
import com.wyz.resource.utils.FileUtils;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 博客控制台相关接口
 */
@Slf4j
@RestController
@RequestMapping("/blog/console")
@Validated
public class BlogConsoleController {

    @Resource
    private BlogService blogService;

    @Resource
    private BlogViewService blogViewService;

    /**
     * 获取创作信息
     */
    @GetMapping("/statistics-data")
    public RestResult<BlogStatisticsDataVO> getStatisticsData() {
        return new RestResult<>(new BlogStatisticsDataVO());
    }

    /**
     * 获取博客列表
     *
     * @param page     当前页
     * @param pageSize 页大小
     * @param status   博客状态码
     * @param authorId 作者id
     * @param month    月份
     */
    @GetMapping("/list")
    public RestResult<BlogListConsoleVO> getBlogList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "0") int status,
            @RequestParam(name = "userId", required = false) Integer authorId,
            @RequestParam(name = "month", required = false) String month) {
        if (pageSize > 200 || pageSize < 1 || status > 10 || status < 0) {
            throw new BusinessException("参数异常");
        }
        if (month != null && !month.matches("\\d{4}-\\d{2}")) {
            throw new BusinessException("月份格式错误, 应为yyyy-MM");
        }
        // 判断要获取的用户id信息
        Integer userId = AuthHelper.getCurrentUserId();
        if (userId == null && authorId == null) {
            throw new BusinessException("参数异常");
        }
        if (!Objects.equals(userId, authorId)) {
            if (authorId == null) {
                authorId = userId;
            } else {
                // 若不是当前用户，则只能查看已发表的博客
                status = BlogStatusType.PUBLISH.getValue();
            }
        }

        // 获取博客列表
        LambdaQueryWrapper<BlogView> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogView::getAuthorId, authorId)
                .eq(status != 0, BlogView::getStatus, status);  // 若status为0, 则查找显示全部博客, 否则查找某部分博客
        // 月份过滤逻辑
        if (StringUtils.isNotBlank(month)) {
            // 解析月份为时间范围
            LocalDate startDate = LocalDate.parse(month + "-01");
            LocalDateTime startOfMonth = startDate.atStartOfDay();
            LocalDateTime endOfMonth = startDate.plusMonths(1).atStartOfDay().minusNanos(1);

            wrapper.between(BlogView::getReleaseTime, startOfMonth, endOfMonth);
        }
        // 使用mybatis进行分页
        IPage<BlogView> blogPage = new Page<>(page, pageSize);
        blogViewService.page(blogPage, wrapper);
        BlogListConsoleVO blogListConsoleVO = new BlogListConsoleVO();
        BeanUtils.copyProperties(blogPage, blogListConsoleVO);
        // 获取博客统计数据
        blogListConsoleVO.setCount(blogService.getBlogCount(authorId));
        return new RestResult<>(blogListConsoleVO);
    }

    /**
     * 控制台搜索个人博客
     *
     * @param page     当前页
     * @param pageSize 页大小
     * @param status   博客状态码
     * @param key      搜索关键字
     * @param month    月份
     */
    @GetMapping("/search")
    public RestResult<BlogListConsoleVO> searchBlog(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "0") int status,
            @RequestParam(name = "key") String key,
            @RequestParam(name = "month", required = false) String month) {
        if (pageSize > 200 || pageSize < 1 || status > 10 || status < 0) {
            throw new BusinessException("参数异常");
        }
        if (month != null && !month.matches("\\d{4}-\\d{2}")) {
            throw new BusinessException("月份格式错误, 应为yyyy-MM");
        }
        Integer userId = AuthHelper.getCurrentUserId();
        // 获取博客列表
        LambdaQueryWrapper<BlogView> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogView::getAuthorId, userId)
                .and(wq -> wq
                        .like(BlogView::getTitle, "%" + key + "%")
                        .or()
                        .like(BlogView::getDescription, "%" + key + "%")
                )
                .eq(status != 0, BlogView::getStatus, status);
        // 月份过滤逻辑
        if (StringUtils.isNotBlank(month)) {
            // 解析月份为时间范围
            LocalDate startDate = LocalDate.parse(month + "-01");
            LocalDateTime startOfMonth = startDate.atStartOfDay();
            LocalDateTime endOfMonth = startDate.plusMonths(1).atStartOfDay().minusNanos(1);

            wrapper.between(BlogView::getReleaseTime, startOfMonth, endOfMonth);
        }
        // 使用mybatis进行分页
        IPage<BlogView> blogPage = new Page<>(page, pageSize);
        blogViewService.page(blogPage, wrapper);
        BlogListConsoleVO blogListConsoleVO = new BlogListConsoleVO();
        BeanUtils.copyProperties(blogPage, blogListConsoleVO);
        // 获取博客统计数据
        blogListConsoleVO.setCount(blogService.getBlogCount(userId));
        return new RestResult<>(blogListConsoleVO);
    }

    /**
     * 保存博客
     *
     * @param blog 要保存的博客内容
     */
    @PostMapping("/blog")
    public void saveBlog(@Validated BlogSaveBO blog, MultipartFile coverImage) {
        Integer id = AuthHelper.getCurrentUserIdOrExit();
        // 设置其他参数
        blog.setCoverImageFile(coverImage);
        blog.setAuthorId(id);
        // 检查封面图
        if (FileUtils.isNotEmpty(blog.getCoverImageFile())) {
            FileUtils.checkFile(blog.getCoverImageFile(), 1024 * 1024L, FileType.JPEG, FileType.PNG);
        }
        blogService.saveBlog(blog);
    }

    /**
     * 删除博客，将博客放入回收站
     *
     * @param id 博客id
     */
    @DeleteMapping("/blog")
    public Boolean deleteBlog(@NotNull Integer id) {
        Integer userId = AuthHelper.getCurrentUserIdOrExit();
        return blogService.deleteBlog(id, userId);
    }

    /**
     * 恢复博客
     *
     * @param id 博客id
     */
    @PutMapping("/blog/recovery")
    public Boolean recoveryBlog(@NotNull Integer id) {
        Integer userId = AuthHelper.getCurrentUserIdOrExit();
        return blogService.recoveryBlog(id, userId);
    }

    /**
     * 彻底删除博客，只有回收站或草稿箱中的博客可以被彻底删除
     *
     * @param id 博客id
     */
    @DeleteMapping("/blog/delete")
    public Boolean completelyDeleteBlog(@NotNull Integer id) {
        Integer userId = AuthHelper.getCurrentUserIdOrExit();
        return blogService.completelyDeleteBlog(id, userId);
    }

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @return 图片链接
     */
    @PostMapping("/image")
    public String uploadBlogImg(@NotNull MultipartFile file) {
        Integer id = AuthHelper.getCurrentUserIdOrExit();
        log.debug("uploadBlogImg, fileName->{}, userId->{}", file.getOriginalFilename(), id);
        FileUtils.checkFile(file, 1024 * 1024L, FileType.JPEG, FileType.PNG);
        return blogService.uploadImage(file);
    }

    /**
     * 获取博客可编辑内容，用于作者编辑博客时调用
     *
     * @param blogId 博客id
     */
    @GetMapping("/content")
    public BlogContent getBlogContent(@RequestParam Integer blogId) {
        Integer id = AuthHelper.getCurrentUserIdOrExit();
        return blogService.getBlogContent(blogId, id);
    }

}
