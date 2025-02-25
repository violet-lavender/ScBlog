package com.wyz.user.pojo;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 用户视图对象, 绑定用户表
 * User 中的 avatarUrl 本身完整字段, 视图对象中才是
 */
@TableName("user_view")
public class UserView extends User {

}
