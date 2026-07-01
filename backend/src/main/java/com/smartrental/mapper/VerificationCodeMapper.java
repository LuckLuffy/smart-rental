package com.smartrental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartrental.entity.VerificationCode;

/**
 * 验证码数据访问层（Mapper）接口
 * <p>
 * 继承 MyBatis-Plus 的 BaseMapper，提供 VerificationCode 实体对应数据库表的
 * 基础 CRUD 操作。主要用于用户注册流程中验证码的
 * 存储、校验和状态更新。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
public interface VerificationCodeMapper extends BaseMapper<VerificationCode> {
}
