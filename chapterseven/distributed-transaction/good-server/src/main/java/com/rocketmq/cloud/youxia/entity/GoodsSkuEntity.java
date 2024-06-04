package com.rocketmq.cloud.youxia.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

//@Data
@TableName(value = "seven_goods_sku")
public class GoodsSkuEntity implements Serializable {
    private Long id;
    private Long goodsId;
    private String skuId;
    private String title;
    private Integer num;
    private Integer numWarnThreshold;
    private Integer sort;
    private BigDecimal shopPrice;
    private BigDecimal costPrice;
    private BigDecimal minSalesPrice;
    private BigDecimal maxSalesPrice;
    private String properties;
    private Long goodsCode;
    private Integer status;
    private Integer isDeleted;
    private Date gmtCreate;
    private Date gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getNumWarnThreshold() {
        return numWarnThreshold;
    }

    public void setNumWarnThreshold(Integer numWarnThreshold) {
        this.numWarnThreshold = numWarnThreshold;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public BigDecimal getShopPrice() {
        return shopPrice;
    }

    public void setShopPrice(BigDecimal shopPrice) {
        this.shopPrice = shopPrice;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getMinSalesPrice() {
        return minSalesPrice;
    }

    public void setMinSalesPrice(BigDecimal minSalesPrice) {
        this.minSalesPrice = minSalesPrice;
    }

    public BigDecimal getMaxSalesPrice() {
        return maxSalesPrice;
    }

    public void setMaxSalesPrice(BigDecimal maxSalesPrice) {
        this.maxSalesPrice = maxSalesPrice;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public Long getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(Long goodsCode) {
        this.goodsCode = goodsCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
}
