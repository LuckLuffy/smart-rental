package com.smartrental.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * <p>
 * 对应数据库中的 user 表，存储系统用户的账号信息。
 * 支持租户（TENANT）和房东（LANDLORD）两种角色，
 * 包含登录凭据、个人基础信息以及账户状态等字段。
 * 使用 MyBatis-Plus 注解实现对象-关系映射（ORM）。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
@TableName("user")
public class User {
    /** 用户ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 用户名，用于登录 */
    private String username;
    /** 登录密码，MD5 加盐加密存储 */
    private String password;
    /** 真实姓名 */
    private String realName;
    /** 手机号码 */
    private String phone;
    /** 电子邮箱地址 */
    private String email;
    /** 角色：TENANT（租客） / LANDLORD（房东） / ADMIN（管理员） */
    private String role;
    /** 性别：0-未知，1-男，2-女 */
    private Integer gender;
    /** 账户状态：1-启用，0-停用 */
    private Integer enabled;
    /** 创建时间，插入时自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /** 更新时间，插入和更新时自动填充 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    /** 逻辑删除标志，0-未删除，1-已删除 */
    @TableLogic
    private Integer deleted;

    // ========== Getter / Setter ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
