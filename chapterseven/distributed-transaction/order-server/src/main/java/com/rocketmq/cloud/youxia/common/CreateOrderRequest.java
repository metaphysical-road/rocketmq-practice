package com.rocketmq.cloud.youxia.common;

import lombok.Data;

//@Data
public class CreateOrderRequest {
    private Long gobalUuid;
    private Long currentUuid;
    private Long goodId;
    private Long userId;
    private Long skuId;
    private Integer num;
    private String price;
    private Long parentUuid;

    public Long getGobalUuid() {
        return gobalUuid;
    }

    public void setGobalUuid(Long gobalUuid) {
        this.gobalUuid = gobalUuid;
    }

    public Long getCurrentUuid() {
        return currentUuid;
    }

    public void setCurrentUuid(Long currentUuid) {
        this.currentUuid = currentUuid;
    }

    public Long getGoodId() {
        return goodId;
    }

    public void setGoodId(Long goodId) {
        this.goodId = goodId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Long getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(Long parentUuid) {
        this.parentUuid = parentUuid;
    }
}
