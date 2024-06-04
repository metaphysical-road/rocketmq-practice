package com.rocketmq.cloud.youxia.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.sql.Date;

//@Data
//@Accessors(chain = true)
@TableName(value = "seven_live_gift")
public class SevenLiveGiftEntity implements Serializable {
    //表示不是表字段
    @TableField(exist = false)
    private Long uk;
    private Long id;
    private String giftName;
    //拥有的礼物总数
    private Integer num;
    //送的礼物数
    @TableField(exist = false)
    private Integer giveNum;
    private Long price;
    private Integer isDeleted;
    private Date gmt_create;
    private Date gmt_modified;
    private Long accountId;
    @TableField(exist = false)
    private String producerClientId;
    @TableField(exist = false)
    private String queryTranStatusProducerInstanceName;
    @Version
    private Integer version;

    public Long getUk() {
        return uk;
    }

    public void setUk(Long uk) {
        this.uk = uk;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getGiveNum() {
        return giveNum;
    }

    public void setGiveNum(Integer giveNum) {
        this.giveNum = giveNum;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getGmt_create() {
        return gmt_create;
    }

    public void setGmt_create(Date gmt_create) {
        this.gmt_create = gmt_create;
    }

    public Date getGmt_modified() {
        return gmt_modified;
    }

    public void setGmt_modified(Date gmt_modified) {
        this.gmt_modified = gmt_modified;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getProducerClientId() {
        return producerClientId;
    }

    public void setProducerClientId(String producerClientId) {
        this.producerClientId = producerClientId;
    }

    public String getQueryTranStatusProducerInstanceName() {
        return queryTranStatusProducerInstanceName;
    }

    public void setQueryTranStatusProducerInstanceName(String queryTranStatusProducerInstanceName) {
        this.queryTranStatusProducerInstanceName = queryTranStatusProducerInstanceName;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
