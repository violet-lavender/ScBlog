package com.wyz.user.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 用户基本信息
 */
@Data
public class UserBasic {

    /**
     * 用户id
     */
    @TableId
    Integer userId;

    /**
     * 用户名
     */
    String username;

    /**
     * 真实姓名
     */
    String realname;

    /**
     * 生日时间戳
     */
    Timestamp birthday;

    /**
     * 个人简介
     */
    String intro;

    /**
     * 性别
     */
    String gender;

    /**
     * 城市id
     */
    Integer cityId;

    /**
     * 省份id
     */
    Integer provinceId;

    /**
     * 信息修改时间
     */
    Timestamp modifiedTime;

    /**
     * 昵称修改时间
     */
    Timestamp nameModifyTime;

    /**
     * 开始工作的时间
     */
    Timestamp startWorkTime;

    /**
     * 是否已经删除，0未删除，1已删除
     */
    Integer deleted;

}
