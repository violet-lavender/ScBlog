package com.wyz.common.web.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wyz.common.result.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 默认的响应包装处理器，拦截所有 Controller 返回的响应体 并进行 统一处理
 * <p>
 * 包装非 RestResult 类型的返回值，确保所有 API 响应都符合 RestResult 结构
 * 特殊处理 String 类型返回值，避免 Spring 的 StringHttpMessageConverter 造成的问题
 */
@Slf4j
@RestControllerAdvice
public class DefaultResponseAdvice implements ResponseBodyAdvice<Object> {

	static ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		// 是否已经被RestResult包装
		boolean isRestResult = returnType.getParameterType().isAssignableFrom(RestResult.class);
		// 是否拒绝默认的ResponseAdvice
		boolean isRefuseAdvice = returnType.hasMethodAnnotation(NotDefaultResponseAdvice.class);
		// 如果没有被包装也没有拒绝默认Advice，则交给beforeBodyWrite进行处理
		return !isRestResult && !isRefuseAdvice;
	}

	@Override
	public Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		// 如果body是String类型，需要特殊处理
		// 这是因为当body为String类型时，转换器类型将会是StringHttpMessageConverter，只能转换String类型的值
		if (body instanceof String) {
			try {
				response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
				return objectMapper.writeValueAsString(RestResult.ok(body));
			} catch (JsonProcessingException e) {
				log.warn("默认包装器处理异常：{}", e.getMessage());
			}
		}
		// 一般类型的对象，直接封装成RestResult返回即可
		return RestResult.ok(body);
	}

}
