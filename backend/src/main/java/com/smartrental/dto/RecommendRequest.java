package com.smartrental.dto;

/**
 * 房源推荐请求 DTO（数据传输对象）
 * <p>
 * 用于封装用户提交的智能推荐请求参数。
 * 用户可以通过填写预算范围、面积需求、户型偏好、位置偏好
 * 以及文字描述等信息，获取系统推荐的匹配房源列表。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
public class RecommendRequest {
    /** 用户对房源的文字描述或特殊需求说明 */
    private String description;
    /** 预算范围，如"2000-3000"（元/月） */
    private String budget;
    /** 面积需求，如"50-80"（平方米） */
    private String area;
    /** 户型偏好，如"一室一厅"、"两室一厅" */
    private String roomType;
    /** 位置偏好，如"朝阳区"、"海淀区" */
    private String location;

    // ========== Getter / Setter ==========

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getBudget() { return budget; }
    public void setBudget(String budget) { this.budget = budget; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
