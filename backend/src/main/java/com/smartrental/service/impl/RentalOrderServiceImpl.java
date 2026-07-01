package com.smartrental.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartrental.entity.RentalOrder;
import com.smartrental.mapper.RentalOrderMapper;
import com.smartrental.service.RentalOrderService;
import org.springframework.stereotype.Service;

/**
 * 租赁订单服务实现类
 * <p>
 * 继承 MyBatis-Plus 的 ServiceImpl，获得对 RentalOrder 实体完整的基础 CRUD 操作。
 * 目前使用 MyBatis-Plus 的默认实现，支持按租客、房东、订单状态等条件
 * 进行灵活的分页查询和数据管理。
 * 如需扩展自定义业务方法（如订单审批、支付处理等），可在
 * {@link RentalOrderService} 接口中定义后在此实现。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
@Service
public class RentalOrderServiceImpl extends ServiceImpl<RentalOrderMapper, RentalOrder> implements RentalOrderService {
}
