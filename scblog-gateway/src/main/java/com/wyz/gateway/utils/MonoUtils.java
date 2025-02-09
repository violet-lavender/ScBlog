package com.wyz.gateway.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

/**
 * Mono 工具类, 构建 Mono<Void> 类型的响应体, 并且将对象转为 JSON 格式返回. 在处理 ServerHttpResponse 时, 用于封装响应的操作
 * <p>
 * 在响应式编程（Reactive Programming）中, Mono 是 Project Reactor 提供的一个重要概念, 它表示一个异步的操作, 最终会返回 零个或一个结果.
 * Mono 是一个封装异步计算结果的容器, 类似于 Future 或 Optional, 但它是非阻塞的, 适用于反应式流式编程.
 */
@Slf4j
public class MonoUtils {

	@SuppressWarnings("AlibabaConstantFieldShouldBeUpperCase")
	private static final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * 构建包装类型，并写入一个Json类型的返回值
	 *
	 * @param response 响应体
	 * @param value    返回值
	 * @return 包装类型
	 */
	public static Mono<Void> buildMonoWrap(ServerHttpResponse response, Object value) {
		return response.writeWith(Mono.fromSupplier(() -> {
			DataBufferFactory bufferFactory = response.bufferFactory();
			try {
				response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
				return bufferFactory.wrap(objectMapper.writeValueAsBytes(value));
			} catch (JsonProcessingException e) {
				log.error("Error writing response", e);
				return bufferFactory.wrap(new byte[0]);
			}
		}));
	}

	/**
	 * 构建包装类型，写入一个Json类型的返回值，并设置http状态码
	 *
	 * @param response   响应体
	 * @param value      返回值
	 * @param httpStatus 状态码
	 * @return 包装类型
	 */
	public static Mono<Void> buildMonoWrap(ServerHttpResponse response, Object value, HttpStatus httpStatus) {
		// 设置状态码
		if (httpStatus != null) {
			response.setStatusCode(httpStatus);
		}
		return buildMonoWrap(response, value);
	}

}
