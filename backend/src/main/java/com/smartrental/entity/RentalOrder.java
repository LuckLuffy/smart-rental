package com.smartrental.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 租赁订单实体类
 * <p>
 * 对应数据库中的 rental_order 表，存储租客与房东之间的租赁订单信息。
 * 包含租赁的房源、租期（起止日期）、总价、订单状态等字段。
 * 租客和房东通过 tenantId / landlordId 关联到用户表中的记录。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
@TableName("rental_order")
public class RentalOrder {
    /** 订单ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 房源ID，关联 house 表 */
    private Long houseId;
    /** 租客用户ID，关联 user 表 */
    private Long tenantId;
    /** 房东用户ID，关联 user 表 */
    private Long landlordId;
    /** 租赁开始日期 */
    private LocalDate startDate;
    /** 租赁结束日期 */
    private LocalDate endDate;
    /** 订单总金额 */
    private BigDecimal totalAmount;
    /** 订单状态：PENDING-待确认，CONFIRMED-已确认，COMPLETED-已完成，CANCELLED-已取消 */
    private String status;
    /** 创建时间，插入时自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /** 更新时间，插入和更新时自动填充 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    /** 逻辑删除标志，0-未删除，1-已删除 */
    @TableLogic
    private Integer deleted;

    // ========== Getter / Setter ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getHouseId() { return houseId; }
    public void setHouseId(Long houseId) { this.houseId = houseId; }
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public Long getLandlordId() { return landlordId; }
    public void setLandlordId(Long landlordId) { this.landlordId = landlordId; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
