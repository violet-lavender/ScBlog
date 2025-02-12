package com.wyz.blink;

import com.wyz.common.tool.mybatisconfig.EnableMybatisPlusIPage;
import com.wyz.common.web.advice.EnableDefaultExceptionAdvice;
import com.wyz.common.web.advice.EnableDefaultResponseAdvice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 动态服务启动类
 */
@SpringBootApplication
@EnableMybatisPlusIPage
@EnableDefaultExceptionAdvice
@EnableDefaultResponseAdvice
@EnableFeignClients(basePackages = {"com.wyz.user.client"})
public class BlinkServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlinkServerApplication.class, args);
	}

}
