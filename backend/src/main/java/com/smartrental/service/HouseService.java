package com.smartrental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartrental.entity.House;

/**
 * 房源服务接口
 * <p>
 * 继承 MyBatis-Plus 的 IService，提供 House 实体的基础 CRUD 操作。
 * 目前使用 MyBatis-Plus 提供的默认实现，无需额外定义业务方法。
 * 实现类为 {@link com.smartrental.service.impl.HouseServiceImpl}。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
public interface HouseService extends IService<House> {
}
