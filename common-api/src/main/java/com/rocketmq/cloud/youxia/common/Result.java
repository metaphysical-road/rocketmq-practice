package com.rocketmq.cloud.youxia.common;

import java.io.Serializable;

public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long errorCode;
    private String msg;
    private T data;

    public Long getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(Long errorCode) {
        this.errorCode = errorCode;
    }

    public Result(Long errorCode, String msg, T data) {
        this.errorCode = errorCode;
        this.msg = msg;
        this.data = data;
    }

    public Result(Long errorCode, String msg) {
        this.errorCode = errorCode;
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return this.data;
    }

    @Override
    public String toString() {
        return "Result [errorCode=" + this.errorCode + ", msg=" + this.msg + ", data=" + this.data + "]";
    }

    public static <T> Result<T> success(T data) {
        return new Result(CommonCode.SUCCESS.getCode(), CommonCode.SUCCESS.getMsg(), data);
    }

    public static <T> Result<T> error(CodeInterface code) {
        return new Result(code.getCode(), code.getMsg());
    }

    public static Boolean isSuccess(Result result) {
        return result != null && CommonCode.SUCCESS.getCode().equals(result.getErrorCode());
    }
}
