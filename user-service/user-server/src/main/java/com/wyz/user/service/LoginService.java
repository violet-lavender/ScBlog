package com.wyz.user.service;

import com.wyz.user.pojo.UserLoginBO;

/**
 * @author 阿杆
 */
public interface LoginService {

	/**
	 * 用户登录服务
	 *
	 * @param username 用户名
	 * @param password 密码
	 * @return 匹配到的信息
	 */
	UserLoginBO login(String username, String password);

}
