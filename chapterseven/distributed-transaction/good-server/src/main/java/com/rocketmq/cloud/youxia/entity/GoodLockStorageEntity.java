package com.rocketmq.cloud.youxia.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

//@Data
@TableName(value = "seven_good_lock_storage")
public class GoodLockStorageEntity implements Serializable {
    private Long id;
    private Long userId;
    private Long goodId;
    private String skuId;
    private Integer lockNum;
    private Integer status;
    private String lockPrice;
    private Integer isDeleted;
    private Date gmtCreate;
    private Date gmtModified;
    @TableField(exist = false)
    private String producerClientId;
    @TableField(exist = false)
    private Long uk;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGoodId() {
        return goodId;
    }

    public void setGoodId(Long goodId) {
        this.goodId = goodId;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public Integer getLockNum() {
        return lockNum;
    }

    public void setLockNum(Integer lockNum) {
        this.lockNum = lockNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getLockPrice() {
        return lockPrice;
    }

    public void setLockPrice(String lockPrice) {
        this.lockPrice = lockPrice;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getProducerClientId() {
        return producerClientId;
    }

    public void setProducerClientId(String producerClientId) {
        this.producerClientId = producerClientId;
    }

    public Long getUk() {
        return uk;
    }

    public void setUk(Long uk) {
        this.uk = uk;
    }
}
