package com.gooodh.model.po;

import com.gooodh.model.enums.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用响应结果类
 * @param <T> 响应数据类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true) // 启用链式调用
public class Result<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 业务状态码：
     * 0 - 成功
     * 1 - 失败
     * 401 - Token过期
     * 403 - 权限不足
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    // ====================== 静态工厂方法 ====================== //
    /** 成功（带数据） */
    public static <E> Result<E> success(E data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /** 成功（无数据） */
    public static <E> Result<E> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /** 自定义成功消息（带数据） */
    public static <E> Result<E> success(String message, E data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /** 失败（默认状态） */
    public static <E> Result<E> failure(String message) {
        return new Result<>(ResultCode.FAILURE.getCode(), message, null);
    }

    public static <E> Result<E> failure(String message, Integer code) {
        return new Result<>(code, message, null);
    }

    /** 使用指定 ResultCode 返回 */
    public static <E> Result<E> of(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /** 使用指定 ResultCode + 数据 返回 */
    public static <E> Result<E> of(ResultCode resultCode, E data) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), data);
    }

    /** 自定义完整响应 */
    public static <E> Result<E> of(Integer code, String message, E data) {
        return new Result<>(code, message, data);
    }

    // ====================== 链式调用支持 ====================== //

    public Result<T> code(Integer code) {
        this.code = code;
        return this;
    }

    public Result<T> message(String message) {
        this.message = message;
        return this;
    }

    public Result<T> data(T data) {
        this.data = data;
        return this;
    }
}
