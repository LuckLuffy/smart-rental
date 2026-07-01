package com.smartrental.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 验证码实体类
 * <p>
 * 对应数据库中的 verification_code 表，存储发送给用户的邮箱验证码。
 * 支持不同类型的验证码（如注册验证码 REGISTER），
 * 包含过期时间（expiresAt）和使用状态（used）字段，
 * 用于验证码的有效性校验和防重复使用。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
@TableName("verification_code")
public class VerificationCode {
    /** 验证码记录ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 接收验证码的电子邮箱地址 */
    private String email;
    /** 验证码内容（6位数字） */
    private String code;
    /** 验证码类型：REGISTER（注册）等 */
    private String type;
    /** 验证码过期时间 */
    private LocalDateTime expiresAt;
    /** 是否已使用：0-未使用，1-已使用 */
    private Integer used;
    /** 创建时间，插入时自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // ========== Getter / Setter ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public Integer getUsed() { return used; }
    public void setUsed(Integer used) { this.used = used; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
