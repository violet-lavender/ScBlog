package com.wyz.resource;

import com.wyz.common.web.advice.EnableDefaultExceptionAdvice;
import com.wyz.common.web.advice.EnableDefaultResponseAdvice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 阿杆
 */
@SpringBootApplication
@EnableDefaultExceptionAdvice
@EnableDefaultResponseAdvice
public class ResourceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResourceServerApplication.class, args);
    }

}
