package com.smartrental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartrental.entity.House;

/**
 * 房源数据访问层（Mapper）接口
 * <p>
 * 继承 MyBatis-Plus 的 BaseMapper，提供 House 实体对应数据库表的
 * 基础 CRUD 操作。支持通过 MyBatis-Plus 的条件构造器（LambdaQueryWrapper）
 * 进行复杂的条件查询、分页查询等。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
public interface HouseMapper extends BaseMapper<House> {
}
