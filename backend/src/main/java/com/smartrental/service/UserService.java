package com.smartrental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartrental.entity.User;

/**
 * 用户服务接口
 * <p>
 * 继承 MyBatis-Plus 的 IService，提供 User 实体的基础 CRUD 操作。
 * 同时定义了用户登录、注册和发送验证码等业务方法。
 * 实现类为 {@link com.smartrental.service.impl.UserServiceImpl}。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录验证
     * <p>
     * 根据用户名和密码校验用户身份，密码需经过 MD5 加盐加密后比对。
     * 仅返回已启用（enabled=1）的用户。
     * </p>
     *
     * @param username 用户名
     * @param password 明文密码（方法内部自动加密比对）
     * @return 登录成功返回用户对象（不含密码），失败返回 null
     */
    User login(String username, String password);

    /**
     * 用户注册
     * <p>
     * 注册流程包含：校验用户名唯一性 -> 校验邮箱验证码 -> 标记验证码已使用
     * -> MD5 加密密码 -> 保存用户。注册用户默认角色为 TENANT（租客）。
     * </p>
     *
     * @param user 待注册的用户信息（用户名、密码、邮箱等）
     * @param code 邮箱验证码
     * @return 注册成功的用户对象
     * @throws RuntimeException 当用户名已存在、验证码错误或验证码过期时抛出
     */
    User register(User user, String code);

    /**
     * 发送邮箱注册验证码
     * <p>
     * 生成 6 位随机数字验证码，保存到数据库中并发送至目标邮箱。
     * 验证码有效期为 5 分钟，且 60 秒内不允许重复发送。
     * </p>
     *
     * @param email 目标邮箱地址
     * @throws RuntimeException 当发送过于频繁时抛出
     */
    void sendVerificationCode(String email);
}
