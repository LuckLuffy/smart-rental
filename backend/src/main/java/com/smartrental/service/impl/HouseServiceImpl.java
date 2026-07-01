package com.smartrental.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartrental.entity.House;
import com.smartrental.mapper.HouseMapper;
import com.smartrental.service.HouseService;
import org.springframework.stereotype.Service;

/**
 * 房源服务实现类
 * <p>
 * 继承 MyBatis-Plus 的 ServiceImpl，获得对 House 实体完整的基础 CRUD 操作。
 * 目前使用 MyBatis-Plus 的默认实现，无需额外业务逻辑。
 * 如需扩展自定义业务方法，可在 {@link HouseService} 接口中定义后在此实现。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
@Service
public class HouseServiceImpl extends ServiceImpl<HouseMapper, House> implements HouseService {
}
