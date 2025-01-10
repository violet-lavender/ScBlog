package com.wyz.user.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户登录业务对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserLoginBO extends User {

    /**
     * 院校名称
     */
    String schoolName;

}
