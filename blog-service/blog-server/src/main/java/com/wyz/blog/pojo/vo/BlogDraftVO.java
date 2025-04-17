package com.wyz.blog.pojo.vo;

import lombok.Data;

/**
 * 博客草稿VO
 */
@Data
public class BlogDraftVO {

    /**
     * 博客 id
     */
    Integer id;

    /**
     * 博客标题
     */
    String title;

    /**
     * 博客内容
     */
    String content;
}
