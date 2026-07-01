package com.smartrental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartrental.common.Result;
import com.smartrental.dto.RecommendRequest;
import com.smartrental.dto.RecommendResult;
import com.smartrental.entity.House;
import com.smartrental.service.DeepSeekService;
import com.smartrental.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房源管理控制器
 * <p>
 * 提供房源相关的 RESTful API 接口，包括房源分页查询、
 * 房源详情查看、房源信息的增删改操作，以及基于 AI 的
 * 智能房源推荐功能。所有接口通过 /api/house 路径访问。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/house")
public class HouseController {

    /** 房源服务，处理房源相关的业务逻辑 */
    @Autowired
    private HouseService houseService;

    /** DeepSeek AI 智能推荐服务 */
    @Autowired
    private DeepSeekService deepSeekService;

    /**
     * 房源分页查询接口
     * <p>
     * GET /api/house/page?current=1&size=10&title=&status=&landlordId=
     * 支持按标题模糊查询、按状态精确筛选和按房东 ID 筛选。
     * 结果按创建时间降序排列（最新房源在前）。
     * </p>
     *
     * @param current    当前页码，默认值 1
     * @param size       每页条数，默认值 10
     * @param title      房源标题（可选，支持模糊查询）
     * @param status     房源状态（可选，如 AVAILABLE/RENTED）
     * @param landlordId 房东用户 ID（可选，精确匹配）
     * @return 分页数据，包含房源列表和分页信息
     */
    @GetMapping("/page")
    public Result<Page<House>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long landlordId) {
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.isEmpty()) wrapper.like(House::getTitle, title);
        if (status != null && !status.isEmpty()) wrapper.eq(House::getStatus, status);
        if (landlordId != null) wrapper.eq(House::getLandlordId, landlordId);
        wrapper.orderByDesc(House::getCreateTime);  // 按创建时间降序排列
        return Result.success(houseService.page(new Page<>(current, size), wrapper));
    }

    /**
     * 根据 ID 获取房源详情接口
     * <p>
     * GET /api/house/{id}
     * </p>
     *
     * @param id 房源 ID
     * @return 房源详细信息
     */
    @GetMapping("/{id}")
    public Result<House> getById(@PathVariable Long id) {
        return Result.success(houseService.getById(id));
    }

    /**
     * 新增房源接口
     * <p>
     * POST /api/house
     * </p>
     *
     * @param house 房源信息
     * @return 是否新增成功
     */
    @PostMapping
    public Result<Boolean> save(@RequestBody House house) {
        return Result.success(houseService.save(house));
    }

    /**
     * 更新房源信息接口
     * <p>
     * PUT /api/house
     * </p>
     *
     * @param house 待更新的房源信息
     * @return 是否更新成功
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody House house) {
        return Result.success(houseService.updateById(house));
    }

    /**
     * 删除房源接口（逻辑删除）
     * <p>
     * DELETE /api/house/{id}
     * 使用 MyBatis-Plus 的逻辑删除功能，将房源标记为已删除而非物理删除。
     * </p>
     *
     * @param id 房源 ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(houseService.removeById(id));
    }

    /**
     * 智能房源推荐接口
     * <p>
     * POST /api/house/recommend
     * 基于用户填写的偏好信息（预算、面积、户型、位置、文字描述等），
     * 从当前状态为 AVAILABLE（可租）的房源中，利用 DeepSeek AI
     * 大模型进行智能匹配和评分推荐。当 AI 服务不可用时自动降级为
     * 基于规则的基础推荐方案。
     * </p>
     *
     * @param req 用户推荐请求，包含偏好信息
     * @return 按匹配度评分降序排列的推荐结果列表
     */
    @PostMapping("/recommend")
    public Result<List<RecommendResult>> recommend(@RequestBody RecommendRequest req) {
        // 筛选出所有可租房源作为推荐候选池
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(House::getStatus, "AVAILABLE");
        List<House> houses = houseService.list(wrapper);
        // 调用 AI 推荐服务进行智能匹配
        List<RecommendResult> results = deepSeekService.recommend(req, houses);
        return Result.success(results);
    }
}
