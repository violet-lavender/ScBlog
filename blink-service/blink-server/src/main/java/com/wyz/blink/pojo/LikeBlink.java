package com.wyz.blink.pojo;


import lombok.Data;

import java.sql.Timestamp;

/**
 * 动态点赞信息类
 */
@Data
public class LikeBlink {

    /**
     * id
     */
    Integer id;

    /**
     * 博客id
     */
    Integer blinkId;

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

