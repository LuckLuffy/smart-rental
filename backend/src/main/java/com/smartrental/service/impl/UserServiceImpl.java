package com.smartrental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartrental.entity.User;
import com.smartrental.entity.VerificationCode;
import com.smartrental.mapper.UserMapper;
import com.smartrental.mapper.VerificationCodeMapper;
import com.smartrental.service.MailService;
import com.smartrental.service.UserService;
import com.smartrental.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * 用户服务实现类
 * <p>
 * 实现 UserService 接口中定义的业务逻辑，包括用户登录验证、
 * 用户注册（含验证码校验）和发送邮箱验证码等功能。
 * 继承 MyBatis-Plus 的 ServiceImpl 获得基础 CRUD 能力，
 * 同时通过 UserMapper 和 VerificationCodeMapper 操作数据库。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /** 验证码数据访问层，用于操作 verification_code 表 */
    @Autowired
    private VerificationCodeMapper codeMapper;

    /** 邮件发送服务 */
    @Autowired
    private MailService mailService;

    /**
     * 用户登录验证
     * <p>
     * 将传入的明文密码进行 MD5 加盐加密后，与数据库中存储的密文进行比对。
     * 同时校验用户状态为已启用（enabled=1）。
     * </p>
     *
     * @param username 用户名
     * @param password 明文密码
     * @return 匹配的用户对象（含完整信息），若不匹配则返回 null
     */
    @Override
    public User login(String username, String password) {
        // 对明文密码进行 MD5 加盐加密后再与数据库中密文比对
        String md5Password = MD5Util.md5(password);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
               .eq(User::getPassword, md5Password)
               .eq(User::getEnabled, 1);  // 仅允许已启用的用户登录
        return getOne(wrapper);
    }

    /**
     * 用户注册
     * <p>
     * 完整的注册校验流程：
     * 1. 检查用户名是否已存在，防止重复注册
     * 2. 校验邮箱验证码的正确性和有效性（是否过期）
     * 3. 标记验证码为已使用，防止重复利用
     * 4. 对密码进行 MD5 加盐加密后存储
     * 5. 默认分配 TENANT（租客）角色并启用账户
     * </p>
     *
     * @param user 用户注册信息
     * @param code 邮箱验证码
     * @return 注册成功的用户对象（已持久化）
     * @throws RuntimeException 用户名已存在、验证码错误或验证码过期时抛出
     */
    @Override
    public User register(User user, String code) {
        // 1. 校验用户名唯一性
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (getOne(wrapper) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 校验邮箱验证码：查询该邮箱最新的一条未使用的 REGISTER 类型验证码
        LambdaQueryWrapper<VerificationCode> codeWrapper = new LambdaQueryWrapper<>();
        codeWrapper.eq(VerificationCode::getEmail, user.getEmail())
                   .eq(VerificationCode::getCode, code)
                   .eq(VerificationCode::getType, "REGISTER")
                   .eq(VerificationCode::getUsed, 0)           // 未使用
                   .orderByDesc(VerificationCode::getCreateTime) // 取最新
                   .last("LIMIT 1");
        VerificationCode vc = codeMapper.selectOne(codeWrapper);
        if (vc == null) {
            throw new RuntimeException("验证码错误");
        }
        // 检查验证码是否已过期
        if (vc.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("验证码已过期，请重新获取");
        }

        // 3. 标记该验证码为已使用（防重复利用）
        vc.setUsed(1);
        codeMapper.updateById(vc);

        // 4. 密码 MD5 加密存储，设置默认角色和启用状态
        user.setPassword(MD5Util.md5(user.getPassword()));
        user.setRole("TENANT");  // 默认注册为租客角色
        user.setEnabled(1);       // 默认启用
        save(user);
        return user;
    }

    /**
     * 发送邮箱注册验证码
     * <p>
     * 发送前检查 60 秒内是否已向同一邮箱发送过验证码，避免频繁发送。
     * 生成 6 位随机数字验证码，保存到数据库（含过期时间 5 分钟），
     * 然后通过 MailService 异步发送至用户邮箱。
     * </p>
     *
     * @param email 目标邮箱地址
     * @throws RuntimeException 发送过于频繁（60秒内重复发送）时抛出
     */
    @Override
    public void sendVerificationCode(String email) {
        // 检查最近 60 秒内是否已向该邮箱发送过验证码，防止接口滥用
        LambdaQueryWrapper<VerificationCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VerificationCode::getEmail, email)
               .eq(VerificationCode::getType, "REGISTER")
               .orderByDesc(VerificationCode::getCreateTime)
               .last("LIMIT 1");
        VerificationCode last = codeMapper.selectOne(wrapper);
        if (last != null && last.getCreateTime().plusSeconds(60).isAfter(LocalDateTime.now())) {
            throw new RuntimeException("发送过于频繁，请60秒后再试");
        }

        // 生成 6 位随机数字验证码（不足6位前补零）
        String code = String.format("%06d", new Random().nextInt(999999));

        // 将验证码记录持久化到数据库
        VerificationCode vc = new VerificationCode();
        vc.setEmail(email);
        vc.setCode(code);
        vc.setType("REGISTER");
        vc.setExpiresAt(LocalDateTime.now().plusMinutes(5));  // 5分钟后过期
        vc.setUsed(0);  // 初始状态：未使用
        codeMapper.insert(vc);

        // 通过邮件服务将验证码发送给用户
        mailService.sendVerificationCode(email, code);
    }
}
