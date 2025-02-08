package com.wyz.common.amqp.autoconfig;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 AMQP 消息转换器配置的注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AmqpMessageConverterConfig.class)
public @interface EnableAmqpMessageConverterConfig {

}
