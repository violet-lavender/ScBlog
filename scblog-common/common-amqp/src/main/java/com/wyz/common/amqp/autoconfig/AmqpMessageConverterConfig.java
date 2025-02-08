package com.wyz.common.amqp.autoconfig;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AMQP消息转换器, 消息的序列化和反序列化
 */
@Configuration
public class AmqpMessageConverterConfig {

	@Bean
	public MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}

}
