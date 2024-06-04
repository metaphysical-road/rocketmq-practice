package com.rocketmq.cloud.youxia.dto;
import lombok.Data;

//@Data
public class LockStorageMessageDTO {
    private String processOnUuid;
    private String gobalUuid;
    private String goodId;
    private String skuId;
    private String num;

    public String getProcessOnUuid() {
        return processOnUuid;
    }

    public void setProcessOnUuid(String processOnUuid) {
        this.processOnUuid = processOnUuid;
    }

    public String getGobalUuid() {
        return gobalUuid;
    }

    public void setGobalUuid(String gobalUuid) {
        this.gobalUuid = gobalUuid;
    }

    public String getGoodId() {
        return goodId;
    }

    public void setGoodId(String goodId) {
        this.goodId = goodId;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
