package com.smartrental.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 插件配置类
 * <p>
 * 配置 MyBatis-Plus 的各项插件功能，当前主要配置了分页插件。
 * 分页插件可以自动拦截带有 Page 参数的查询，自动生成 LIMIT 语句，
 * 无需手动拼接分页 SQL，简化分页查询的开发。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 注册 MyBatis-Plus 拦截器 Bean
     * <p>
     * 添加 MySQL 数据库的分页拦截器（PaginationInnerInterceptor），
     * 支持自动分页查询。当 Service 或 Mapper 方法接收 Page 参数时，
     * 拦截器会自动在 SQL 末尾追加 LIMIT 语句，并计算总记录数。
     * </p>
     *
     * @return MybatisPlusInterceptor 拦截器实例
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加 MySQL 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
