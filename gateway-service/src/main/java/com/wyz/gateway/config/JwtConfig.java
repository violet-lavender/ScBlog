package com.wyz.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 配置类
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

	public void setHeaderName(String headerName) {
		JwtConfig.headerName = headerName;
	}

}
