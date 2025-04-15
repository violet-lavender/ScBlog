package com.wyz.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wyz.blog.pojo.domain.ViewBlog;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 博客浏览信息Mapper
 */
@Mapper
public interface ViewBlogMapper extends BaseMapper<ViewBlog> {

    /**
     * 删除指定用户最早的N条浏览记录(这里直接物理删除了)
     *
     * @param userId
     * @param deleteCount
     */
    @Delete("delete from view_blog where user_id = #{userId} order by create_time asc limit #{deleteCount}")
    void deleteOldestByUser(Integer userId, Integer deleteCount);

    /**
     * 删除用户的浏览记录(这里逻辑删除吧)
     */
    @Update("update view_blog set deleted = 1 where user_id = #{userId}")
    int clearViewBlogs(Integer userId);
}
