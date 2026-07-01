package com.smartrental.dto;

import com.smartrental.entity.House;
import java.util.List;

/**
 * 房源推荐结果 DTO（数据传输对象）
 * <p>
 * 封装智能推荐系统返回的单个房源推荐结果。
 * 包含推荐的房源信息、匹配度评分（0-100）以及推荐理由列表，
 * 用于前端展示推荐结果时提供详细说明。
 * </p>
 *
 * @author SmartRental Team
 * @version 1.0
 */
public class RecommendResult {
    /** 推荐的房源对象 */
    private House house;
    /** 匹配度评分，0-100 分，分数越高表示越匹配 */
    private int score;
    /** 推荐理由列表，如["预算匹配", "交通便利", "户型合适"] */
    private List<String> reasons;

    /** 默认无参构造方法 */
    public RecommendResult() {}

    /**
     * 全参构造方法
     *
     * @param house   推荐的房源对象
     * @param score   匹配度评分
     * @param reasons 推荐理由列表
     */
    public RecommendResult(House house, int score, List<String> reasons) {
        this.house = house;
        this.score = score;
        this.reasons = reasons;
    }

    // ========== Getter / Setter ==========

    public House getHouse() { return house; }
    public void setHouse(House house) { this.house = house; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }
}
