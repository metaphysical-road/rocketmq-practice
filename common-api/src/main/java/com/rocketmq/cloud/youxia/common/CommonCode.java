package com.rocketmq.cloud.youxia.common;

public enum CommonCode implements CodeInterface{
    SUCCESS(0L, "success"),
    SERVER_ERROR(3000001L, "模块错误"),
    TOKEN_INCORRECT(30001005L, "token验证失败"),
    TOKEN_NOT_CHECKED(30001007L, "没有验证token"),
    USER_NOT_EXIST(30002001L, "用户不存在"),
    PARAMETER_PARSE_ERROR(30001011L, "参数解析错误"),
    SIGNATURE_ERROR(3000002L, "签名错误");

    Long code;
    String msg;

    private CommonCode(Long code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Long getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
