package com.wyz.blog;

import com.wyz.common.amqp.autoconfig.EnableAmqpMessageConverterConfig;
import com.wyz.common.redis.autoconfig.EnableRedisSerialize;
import com.wyz.common.tool.mybatisconfig.EnableMybatisPlusIPage;
import com.wyz.common.web.advice.EnableDefaultExceptionAdvice;
import com.wyz.common.web.advice.EnableDefaultResponseAdvice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author 阿杆
 */
@SuppressWarnings("AlibabaCommentsMustBeJavadocFormat")
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.wyz.resource.client", "com.wyz.user.client"}) // 开启feign
@EnableMybatisPlusIPage // 开启mybatis分页
@EnableRedisSerialize
@EnableAmqpMessageConverterConfig
@EnableDefaultExceptionAdvice
@EnableDefaultResponseAdvice
public class BlogServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogServerApplication.class, args);
	}

}
