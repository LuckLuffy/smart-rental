package com.smartrental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartrental.entity.RentalOrder;

/**
 * 租赁订单服务接口
 * <p>
 * 继承 MyBatis-Plus 的 IService，提供 RentalOrder 实体的基础 CRUD 操作。
 * 目前使用 MyBatis-Plus 提供的默认实现，支持按租客、房东、
 * 订单状态等条件进行分页查询。
 * 实现类为 {@link com.smartrental.service.impl.RentalOrderServiceImpl}。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
public interface RentalOrderService extends IService<RentalOrder> {
}
