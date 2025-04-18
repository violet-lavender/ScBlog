package com.wyz.blog.pojo.domain;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 博客浏览信息类
 */
@Data
public class ViewBlog {

    /**
     * id
     */
    Integer id;

    /**
     * 博客id
     */
    Integer blogId;

    /**
     * 用户id
     */
    Integer userId;

    /**
     * 创建时间
     */
    Timestamp createTime;

    /**
     * 是否已经删除，0未删除，1已删除
     */
    Integer deleted;

}
