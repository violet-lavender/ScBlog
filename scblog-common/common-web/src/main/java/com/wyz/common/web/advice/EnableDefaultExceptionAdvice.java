package com.wyz.common.web.advice;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Web程序默认异常处理器开关注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DefaultExceptionAdvice.class)
public @interface EnableDefaultExceptionAdvice {

}
