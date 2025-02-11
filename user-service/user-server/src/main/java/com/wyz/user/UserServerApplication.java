package com.wyz.user;

import com.wyz.common.redis.autoconfig.EnableRedisSerialize;
import com.wyz.common.tool.mybatisconfig.EnableMybatisPlusIPage;
import com.wyz.common.web.advice.EnableDefaultExceptionAdvice;
import com.wyz.common.web.advice.EnableDefaultResponseAdvice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 用户服务启动类
 */
@SuppressWarnings("AlibabaCommentsMustBeJavadocFormat")
@SpringBootApplication
@EnableAsync // 开启异步控制
@EnableFeignClients(basePackages = {"com.wyz.resource.client", "com.wyz.blog.client"}) // 开启feign
@EnableMybatisPlusIPage // 开启mybatis分页助手
@EnableRedisSerialize // 开启RedisTemplate序列化配置
@EnableScheduling // 开启定时任务
@EnableDefaultExceptionAdvice // 注入默认异常处理器
@EnableDefaultResponseAdvice // 注入默认包装器
public class UserServerApplication {
    public static void main(String[] args) {

        SpringApplication.run(UserServerApplication.class, args);
    }

}
