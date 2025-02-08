package com.wyz.common.tool.mybatisconfig;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * MybatisPlus分页功能配置开关注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MybatisPlusDefaultConfig.class)
public @interface EnableMybatisPlusIPage {

}
