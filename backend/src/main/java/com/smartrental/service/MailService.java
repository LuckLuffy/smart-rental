package com.smartrental.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件发送服务类
 * <p>
 * 负责发送系统各类邮件通知，目前主要用于发送用户注册时的邮箱验证码。
 * 使用 Spring Boot 的 JavaMailSender 实现邮件发送功能，
 * 邮件服务器配置在 application.yml 中通过 spring.mail 前缀设置。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
@Service
public class MailService {

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    /** Spring Boot 邮件发送器，由框架自动注入 */
    @Autowired
    private JavaMailSender mailSender;

    /** 发件人邮箱地址，从配置文件中注入 */
    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送邮箱验证码
     * <p>
     * 构造一封包含 6 位数字验证码的简单邮件，发送至目标邮箱。
     * 邮件内容包括验证码、有效期提示和安全提醒。
     * 发送失败时会记录错误日志并抛出运行时异常。
     * </p>
     *
     * @param to   收件人邮箱地址
     * @param code 6 位验证码内容
     * @throws RuntimeException 当邮件发送失败时抛出
     */
    public void sendVerificationCode(String to, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            // 设置发件人、收件人
            message.setFrom(from);
            message.setTo(to);
            // 设置邮件主题和正文
            message.setSubject("【智慧租房】邮箱验证码");
            message.setText("您好！\n\n"
                    + "您的验证码是：" + code + "\n"
                    + "验证码5分钟内有效，请勿泄露给他人。\n\n"
                    + "如非本人操作，请忽略此邮件。\n\n"
                    + "智慧租房管理系统");
            // 执行发送
            mailSender.send(message);
            log.info("验证码邮件已发送至 {}", to);
        } catch (Exception e) {
            log.error("邮件发送失败: {}", e.getMessage(), e);
            throw new RuntimeException("邮件发送失败: " + e.getMessage());
        }
    }
}
