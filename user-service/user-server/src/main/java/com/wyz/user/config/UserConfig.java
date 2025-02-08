package com.wyz.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 绑定配置属性, 管理默认头像配置
 */
@Configuration
@ConfigurationProperties("user")
public class UserConfig {

    public static String DefaultAvatar;

    public void setDefaultAvatar(String defaultAvatar) {
        DefaultAvatar = defaultAvatar;
    }

}
