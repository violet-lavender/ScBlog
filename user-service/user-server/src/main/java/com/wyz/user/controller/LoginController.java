package com.wyz.user.controller;

import com.wyz.common.exception.BusinessException;
import com.wyz.user.config.JwtConfig;
import com.wyz.user.pojo.UserLoginBO;
import com.wyz.user.service.LoginService;
import com.wyz.user.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录相关接口
 */
@Slf4j
@RestController
@RequestMapping("/login")
@Validated
public class LoginController {

    @Resource
    private LoginService loginService;

    @Resource
    private HttpServletResponse response;

    /**
     * 用户登录
     *
     * @param username 账号
     * @param password 密码
     */
    @PostMapping("/login")
    public UserLoginBO loginHandle(@NotNull String username, @NotNull String password) {
        UserLoginBO user = loginService.login(username, password);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        response.setHeader(JwtConfig.headerName, JwtUtils.createToken("id", user.getId()));
        // TODO: 具体信息
        response.setHeader("Access-Control-Expose-Headers", JwtConfig.headerName);
        // 院校代码放到cookie去
        Cookie cookie = new Cookie("schoolCode", user.getSchoolCode().toString());
        cookie.setMaxAge(3600 * 7);
        response.addCookie(cookie);
        return user;
    }

}
