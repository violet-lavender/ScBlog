package com.wyz.blog.content.config;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Es 兼容性配置，添加响应头，兼容服务端版本
 * <p>
 * 由于升级SpringBoot3.x后，没有可用的 es-client-7，只能使用 es-client-8，
 * 而服务端使用的是 7.x 版本，导致客户端无法正常使用，因此需要添加兼容性配置。
 */
@Component
public class EsCompatibilityConfig implements RestClientBuilderCustomizer {

    @Override
    public void customize(RestClientBuilder builder) {
    }

    @Override
    public void customize(HttpAsyncClientBuilder builder) {
        // 添加响应头，兼容X-Elastic-Product
        HttpResponseInterceptor httpResponseInterceptor =
                (response, context) -> response.addHeader("X-Elastic-Product", "Elasticsearch");
        builder.addInterceptorLast(httpResponseInterceptor);
        // 自定义默认请求头，目的是禁用兼容性请求头 compatible-with
        builder.setDefaultHeaders(List.of(new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString())));
    }

}