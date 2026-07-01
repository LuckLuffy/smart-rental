package com.smartrental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartrental.entity.RentalOrder;

/**
 * 租赁订单数据访问层（Mapper）接口
 * <p>
 * 继承 MyBatis-Plus 的 BaseMapper，提供 RentalOrder 实体对应数据库表的
 * 基础 CRUD 操作。支持按租客、房东、订单状态等条件进行
 * 高效的分页查询和数据统计。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
public interface RentalOrderMapper extends BaseMapper<RentalOrder> {
}
