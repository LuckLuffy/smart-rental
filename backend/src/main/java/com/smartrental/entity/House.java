package com.smartrental.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房源实体类
 * <p>
 * 对应数据库中的 house 表，存储房源的详细信息。
 * 包括房源标题、描述、地址、面积、价格、户型类型、状态等。
 * 每个房源关联一个房东（landlordId），并支持逻辑删除。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
@TableName("house")
public class House {
    /** 房源ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 房源标题 */
    private String title;
    /** 房源描述 */
    private String description;
    /** 房源地址 */
    private String address;
    /** 房屋面积（平方米） */
    private BigDecimal area;
    /** 月租金价格 */
    private BigDecimal price;
    /** 房屋类型，如"一室一厅"、"两室一厅"等 */
    private String type;
    /** 房源状态：AVAILABLE-可租，RENTED-已租，MAINTENANCE-维护中 */
    private String status;
    /** 所属房东的用户ID */
    private Long landlordId;
    /** 房源图片URL */
    private String imageUrl;
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
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public BigDecimal getArea() { return area; }
    public void setArea(BigDecimal area) { this.area = area; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getLandlordId() { return landlordId; }
    public void setLandlordId(Long landlordId) { this.landlordId = landlordId; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
