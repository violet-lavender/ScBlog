package com.wyz.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT配置类
 */
@Configuration
@ConfigurationProperties(prefix = "security.jwt")
public class JwtConfig {

    /**
     * 密钥KEY
     */
    public static String key;

    /**
     * 发放者
     */
    public static String issuer;

    /**
     * 过期时间
     */
    public static Integer expiration;

    /**
     * 不需要认证的接口
     */
    public static String matchers;

    /**
     * http头token名
     */
    public static String headerName;

    public void setKey(String secret) {
        JwtConfig.key = secret;
    }

    public void setIssuer(String tokenHeader) {
        JwtConfig.issuer = tokenHeader;
    }

    public void setExpiration(Integer expiration) {
        JwtConfig.expiration = expiration;
    }

    public void setMatchers(String matchers) {
        JwtConfig.matchers = matchers;
    }

    public void setHeaderName(String headerName) {
        JwtConfig.headerName = headerName;
    }

}
