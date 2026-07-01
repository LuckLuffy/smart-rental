package com.smartrental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartrental.entity.User;

/**
 * 用户数据访问层（Mapper）接口
 * <p>
 * 继承 MyBatis-Plus 的 BaseMapper，提供 User 实体对应数据库表的
 * 基础 CRUD（增删改查）操作。无需编写 SQL 映射文件，
 * MyBatis-Plus 会根据实体类上的注解自动生成 SQL 语句。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
public interface UserMapper extends BaseMapper<User> {
}
