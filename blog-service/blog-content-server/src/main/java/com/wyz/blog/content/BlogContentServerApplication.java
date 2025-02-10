package com.wyz.blog.content;

import com.wyz.common.redis.autoconfig.EnableRedisSerialize;
import com.wyz.common.web.advice.EnableDefaultExceptionAdvice;
import com.wyz.common.web.advice.EnableDefaultResponseAdvice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author 阿杆
 * @version 1.0
 * @date 2022/7/7 22:32
 */
@EnableRedisSerialize
@EnableFeignClients(basePackages = "com.wyz.user.client")
@SpringBootApplication
@EnableDefaultExceptionAdvice
@EnableDefaultResponseAdvice
public class BlogContentServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogContentServerApplication.class, args);
	}

}
