package com.wyz.common.web.advice;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Web程序默认响应体包装处理器开关注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DefaultResponseAdvice.class)
public @interface EnableDefaultResponseAdvice {

}
