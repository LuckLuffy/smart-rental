package com.smartrental.common;

/**
 * 统一响应结果封装类
 * <p>
 * 用于封装后端 API 的返回结果，提供统一的响应格式。
 * 包含状态码（code）、提示信息（message）和响应数据（data）三个部分。
 * 通过静态工厂方法快速创建成功或失败的响应对象。
 * </p>
 *
 * @param <T> 响应数据的泛型类型
 * @author SmartRental Team
 * @version 1.0
 */
public class Result<T> {
    /** 状态码，200 表示成功，500 表示服务器错误 */
    private Integer code;
    /** 提示信息 */
    private String message;
    /** 响应数据 */
    private T data;

    /**
     * 私有构造方法，禁止外部直接实例化
     *
     * @param code    状态码
     * @param message 提示信息
     * @param data    响应数据
     */
    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 创建操作成功的响应（无返回数据）
     *
     * @param <T> 数据类型
     * @return 成功响应，code=200，message="操作成功"
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    /**
     * 创建操作成功的响应（带返回数据）
     *
     * @param <T>  数据类型
     * @param data 返回的数据对象
     * @return 成功响应，code=200，message="操作成功"
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 创建操作成功的响应（自定义提示信息，带返回数据）
     *
     * @param <T>     数据类型
     * @param message 自定义成功提示信息
     * @param data    返回的数据对象
     * @return 成功响应，code=200
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 创建操作失败的响应（默认 500 状态码）
     *
     * @param <T>     数据类型
     * @param message 错误提示信息
     * @return 失败响应，code=500
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    /**
     * 创建操作失败的响应（自定义状态码）
     *
     * @param <T>     数据类型
     * @param code    自定义错误状态码
     * @param message 错误提示信息
     * @return 失败响应
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    // ========== Getter / Setter ==========

    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
