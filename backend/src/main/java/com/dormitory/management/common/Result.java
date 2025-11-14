package com.dormitory.management.common;

import lombok.Data;

/**
 * 统一返回结果类
 */
@Data
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error() {
        return error(500, "操作失败");
    }

    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    public static <T> Result<T> unauthorized() {
        return error(401, "未授权");
    }

    public static <T> Result<T> forbidden() {
        return error(403, "权限不足");
    }

    public static <T> Result<T> notFound() {
        return error(404, "资源不存在");
    }
}