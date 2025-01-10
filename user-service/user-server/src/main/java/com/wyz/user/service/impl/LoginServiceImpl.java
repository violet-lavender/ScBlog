package com.wyz.user.service.impl;

import cn.sticki.common.result.RestResult;
import cn.sticki.resource.client.ResourceClient;
import com.wyz.user.mapper.UserSafetyMapper;
import com.wyz.user.mapper.UserViewMapper;
import com.wyz.user.pojo.UserLoginBO;
import com.wyz.user.pojo.UserSafety;
import com.wyz.user.pojo.UserView;
import com.wyz.user.service.LoginService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author 阿杆
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

	@Resource
	private UserSafetyMapper userSafetyMapper;

	@Resource
	private PasswordEncoder encoder;

	@Resource
	private UserViewMapper userViewMapper;

	@Resource
	private ResourceClient resourceClient;

	@Override
	public UserLoginBO login(String username, String password) {
		LambdaQueryWrapper<UserSafety> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(UserSafety::getUsername, username);
		UserSafety user = userSafetyMapper.selectOne(wrapper);
		if (Objects.nonNull(user) && encoder.matches(password, user.getPassword())) {
			// 查询用户信息
			UserView userView = userViewMapper.selectById(user.getUserId());
			// 查询院校名称
			UserLoginBO userLoginBO = new UserLoginBO();
			BeanUtils.copyProperties(userView, userLoginBO);
			RestResult<String> universityName = resourceClient.getUniversityName(userView.getSchoolCode());
			userLoginBO.setSchoolName(universityName.getData());
			return userLoginBO;
		}
		return null;
	}

}
