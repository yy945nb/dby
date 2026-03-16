package com.dby.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * 网关全局过滤器配置
 */
@Configuration
public class GatewayConfig {

    private static final Logger log = LoggerFactory.getLogger(GatewayConfig.class);

    /**
     * 请求日志过滤器 - 记录每个请求的路径和响应时间
     */
    @Bean
    @Order(-1)
    public GlobalFilter requestLoggingFilter() {
        return (exchange, chain) -> {
            long startTime = Instant.now().toEpochMilli();
            String path = exchange.getRequest().getPath().value();
            String method = exchange.getRequest().getMethod().name();

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                long duration = Instant.now().toEpochMilli() - startTime;
                int statusCode = exchange.getResponse().getStatusCode() != null
                        ? exchange.getResponse().getStatusCode().value() : 0;
                log.info("{} {} -> {} ({}ms)", method, path, statusCode, duration);
            }));
        };
    }
}
