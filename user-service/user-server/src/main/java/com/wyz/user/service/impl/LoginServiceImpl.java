package com.wyz.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wyz.common.result.RestResult;
import com.wyz.resource.client.ResourceClient;
import com.wyz.user.mapper.UserSafetyMapper;
import com.wyz.user.mapper.UserViewMapper;
import com.wyz.user.pojo.UserLoginBO;
import com.wyz.user.pojo.UserSafety;
import com.wyz.user.pojo.UserView;
import com.wyz.user.service.LoginService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 用户登录服务实现类
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

    /**
     * 用户登录服务
     *
     * @param username 用户名
     * @param password 密码
     * @return 匹配到的信息
     */
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
