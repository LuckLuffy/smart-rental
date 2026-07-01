package com.smartrental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartrental.common.Result;
import com.smartrental.entity.User;
import com.smartrental.service.UserService;
import com.smartrental.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 用户管理控制器
 * <p>
 * 提供用户相关的 RESTful API 接口，包括登录、注册、邮箱验证码、
 * 用户分页查询、用户详情查看以及用户信息的增删改操作。
 * 所有接口统一通过 /api/user 路径访问，返回统一格式的 Result 响应体。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    /** 用户服务，处理用户相关的业务逻辑 */
    @Autowired
    private UserService userService;

    /**
     * 用户登录接口
     * <p>
     * POST /api/user/login
     * 接收用户名和密码，验证用户身份。成功后返回用户信息（密码字段置空）。
     * </p>
     *
     * @param params 请求体，包含 username 和 password 字段
     * @return 登录成功返回用户信息，失败返回错误提示
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map<String, String> params) {
        User user = userService.login(params.get("username"), params.get("password"));
        if (user != null) {
            user.setPassword(null);  // 返回前清空密码，保障安全
            return Result.success("登录成功", user);
        }
        return Result.error("用户名或密码错误");
    }

    /**
     * 用户注册接口
     * <p>
     * POST /api/user/register
     * 接收用户注册信息和邮箱验证码，完成注册流程。
     * 注册成功后返回用户信息（密码字段置空）。
     * </p>
     *
     * @param params 请求体，包含 username、password、email、code（验证码）、realName 等字段
     * @return 注册成功返回用户信息，失败返回错误提示
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody Map<String, String> params) {
        try {
            User user = new User();
            user.setUsername(params.get("username"));
            user.setPassword(params.get("password"));
            // realName 未传入时默认使用用户名
            user.setRealName(params.get("realName") != null ? params.get("realName") : params.get("username"));
            user.setEmail(params.get("email"));
            String code = params.get("code");

            User registered = userService.register(user, code);
            registered.setPassword(null);  // 返回前清空密码
            return Result.success("注册成功", registered);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 发送邮箱验证码接口
     * <p>
     * POST /api/user/send-code
     * 向指定邮箱发送注册验证码，60 秒内不可重复发送。
     * </p>
     *
     * @param params 请求体，包含 email 字段
     * @return 发送成功返回成功提示，失败返回错误信息
     */
    @PostMapping("/send-code")
    public Result<Boolean> sendCode(@RequestBody Map<String, String> params) {
        try {
            String email = params.get("email");
            if (email == null || email.isEmpty()) {
                return Result.error("邮箱不能为空");
            }
            userService.sendVerificationCode(email);
            return Result.success("验证码已发送", true);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户分页查询接口
     * <p>
     * GET /api/user/page?current=1&size=10&username=&role=
     * 支持按用户名模糊查询和按角色精确筛选，结果按用户 ID 升序排列。
     * 返回前清空所有用户的密码字段。
     * </p>
     *
     * @param current 当前页码，默认值 1
     * @param size    每页条数，默认值 10
     * @param username 用户名（可选，支持模糊查询）
     * @param role     用户角色（可选，精确匹配）
     * @return 分页数据，包含用户列表和分页信息
     */
    @GetMapping("/page")
    public Result<Page<User>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) wrapper.like(User::getUsername, username);
        if (role != null && !role.isEmpty()) wrapper.eq(User::getRole, role);
        wrapper.orderByAsc(User::getId);
        Page<User> page = userService.page(new Page<>(current, size), wrapper);
        // 清空密码，防止敏感信息泄露
        page.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(page);
    }

    /**
     * 根据 ID 获取用户详情接口
     * <p>
     * GET /api/user/{id}
     * </p>
     *
     * @param id 用户 ID
     * @return 用户详细信息（密码字段置空）
     */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 新增用户接口
     * <p>
     * POST /api/user
     * 新增用户前对密码进行 MD5 加密处理。
     * </p>
     *
     * @param user 用户信息
     * @return 是否新增成功
     */
    @PostMapping
    public Result<Boolean> save(@RequestBody User user) {
        // 密码非空时进行 MD5 加盐加密后存储
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(MD5Util.md5(user.getPassword()));
        }
        return Result.success(userService.save(user));
    }

    /**
     * 更新用户信息接口
     * <p>
     * PUT /api/user
     * 如果请求中传入了新密码，自动进行 MD5 加密后更新。
     * </p>
     *
     * @param user 待更新的用户信息
     * @return 是否更新成功
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody User user) {
        // 如果传入了新密码，进行 MD5 加盐加密后存储
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(MD5Util.md5(user.getPassword()));
        }
        return Result.success(userService.updateById(user));
    }

    /**
     * 删除用户接口（逻辑删除）
     * <p>
     * DELETE /api/user/{id}
     * 使用 MyBatis-Plus 的逻辑删除功能，将用户标记为已删除而非物理删除。
     * </p>
     *
     * @param id 用户 ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(userService.removeById(id));
    }
}
