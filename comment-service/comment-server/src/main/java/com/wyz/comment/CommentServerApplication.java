package com.wyz.comment;

import com.wyz.common.tool.mybatisconfig.EnableMybatisPlusIPage;
import com.wyz.common.web.advice.EnableDefaultExceptionAdvice;
import com.wyz.common.web.advice.EnableDefaultResponseAdvice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 评论服务启动类
 */
@SpringBootApplication
@EnableMybatisPlusIPage
@EnableFeignClients(basePackages = {"com.wyz.blog.client", "com.wyz.user.client"})
@EnableDefaultExceptionAdvice
@EnableDefaultResponseAdvice
public class CommentServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommentServerApplication.class, args);
    }

}
