package com.wyz.gateway.filter;

import com.wyz.gateway.pojo.VisitRecord;
import com.wyz.gateway.service.VisitRecordService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 访问记录过滤器, 保存访问记录, 最先执行. 用过滤器而不是切面、拦截器
 */
@Slf4j
@Component
public class VisitRecordFilter implements GlobalFilter, Ordered {

	@Resource
	VisitRecordService visitRecordService;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		VisitRecord visitRecord = visitRecordService.build(exchange);

		visitRecordService.put(exchange, visitRecord);
		// 请求后执行保存
		return chain.filter(exchange).then(saveRecord(exchange));
	}

	private Mono<Void> saveRecord(ServerWebExchange exchange) {
		return Mono.fromRunnable(() -> visitRecordService.add(exchange));
	}

	@Override
	public int getOrder() {
		return -10000;
	}

}
