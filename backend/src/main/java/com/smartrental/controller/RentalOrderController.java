package com.smartrental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartrental.common.Result;
import com.smartrental.entity.RentalOrder;
import com.smartrental.service.RentalOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 租赁订单管理控制器
 * <p>
 * 提供租赁订单相关的 RESTful API 接口，包括订单分页查询、
 * 订单详情查看、以及订单的增删改操作。支持按租客、房东
 * 和订单状态进行筛选。所有接口通过 /api/order 路径访问。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/order")
public class RentalOrderController {

    /** 租赁订单服务，处理订单相关的业务逻辑 */
    @Autowired
    private RentalOrderService orderService;

    /**
     * 订单分页查询接口
     * <p>
     * GET /api/order/page?current=1&size=10&tenantId=&landlordId=&status=
     * 支持按租客 ID、房东 ID 和订单状态进行筛选。
     * 结果按创建时间降序排列（最新订单在前）。
     * </p>
     *
     * @param current    当前页码，默认值 1
     * @param size       每页条数，默认值 10
     * @param tenantId   租客用户 ID（可选，精确匹配）
     * @param landlordId 房东用户 ID（可选，精确匹配）
     * @param status     订单状态（可选，如 PENDING/CONFIRMED/COMPLETED/CANCELLED）
     * @return 分页数据，包含订单列表和分页信息
     */
    @GetMapping("/page")
    public Result<Page<RentalOrder>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) Long landlordId,
            @RequestParam(required = false) String status) {
        LambdaQueryWrapper<RentalOrder> wrapper = new LambdaQueryWrapper<>();
        if (tenantId != null) wrapper.eq(RentalOrder::getTenantId, tenantId);
        if (landlordId != null) wrapper.eq(RentalOrder::getLandlordId, landlordId);
        if (status != null && !status.isEmpty()) wrapper.eq(RentalOrder::getStatus, status);
        wrapper.orderByDesc(RentalOrder::getCreateTime);  // 按创建时间降序排列
        return Result.success(orderService.page(new Page<>(current, size), wrapper));
    }

    /**
     * 根据 ID 获取订单详情接口
     * <p>
     * GET /api/order/{id}
     * </p>
     *
     * @param id 订单 ID
     * @return 订单详细信息
     */
    @GetMapping("/{id}")
    public Result<RentalOrder> getById(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }

    /**
     * 新增订单接口
     * <p>
     * POST /api/order
     * </p>
     *
     * @param order 订单信息
     * @return 是否新增成功
     */
    @PostMapping
    public Result<Boolean> save(@RequestBody RentalOrder order) {
        return Result.success(orderService.save(order));
    }

    /**
     * 更新订单信息接口
     * <p>
     * PUT /api/order
     * </p>
     *
     * @param order 待更新的订单信息
     * @return 是否更新成功
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody RentalOrder order) {
        return Result.success(orderService.updateById(order));
    }

    /**
     * 删除订单接口（逻辑删除）
     * <p>
     * DELETE /api/order/{id}
     * 使用 MyBatis-Plus 的逻辑删除功能，将订单标记为已删除而非物理删除。
     * </p>
     *
     * @param id 订单 ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(orderService.removeById(id));
    }
}
