package com.smartrental;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 智慧租房管理系统 - 主启动类
 * <p>
 * 作为 Spring Boot 应用程序的入口，负责初始化整个应用程序上下文。
 * 通过 @SpringBootApplication 注解启用自动配置、组件扫描和属性配置支持。
 * 通过 @MapperScan 注解自动扫描并注册 MyBatis-Plus 的 Mapper 接口。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
@SpringBootApplication
@MapperScan("com.smartrental.mapper")
public class SmartRentalApplication {

    /**
     * 应用程序主入口方法
     * <p>
     * 调用 SpringApplication.run() 启动 Spring Boot 应用，
     * 完成内置 Tomcat 服务器启动、IoC 容器初始化、自动配置加载等流程。
     * </p>
     *
     * @param args 命令行传入的启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(SmartRentalApplication.class, args);
    }
}
