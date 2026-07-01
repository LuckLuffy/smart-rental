package com.smartrental.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域资源共享（CORS）配置类
 * <p>
 * 允许前端应用在不同域名或端口下访问后端 API。
 * 配置了允许的来源、请求头、请求方法，并支持携带凭证（Cookie/Session）。
 * 使用 CorsFilter 拦截所有 /api 路径的请求，添加跨域响应头。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
@Configuration
public class CorsConfig {

    /**
     * 注册跨域过滤器 Bean
     * <p>
     * 配置允许任意来源（addAllowedOriginPattern("*")）、
     * 任意请求头和任意 HTTP 方法（GET/POST/PUT/DELETE 等），
     * 并允许跨域请求携带凭证（Cookies）。
     * </p>
     *
     * @return CorsFilter 跨域过滤器实例
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有来源的跨域请求
        config.addAllowedOriginPattern("*");
        // 允许携带所有请求头
        config.addAllowedHeader("*");
        // 允许所有 HTTP 方法
        config.addAllowedMethod("*");
        // 允许跨域请求携带凭证（如 Cookie）
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径生效
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
