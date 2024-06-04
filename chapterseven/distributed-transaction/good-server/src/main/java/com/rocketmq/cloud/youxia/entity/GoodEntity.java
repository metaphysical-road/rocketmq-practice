package com.rocketmq.cloud.youxia.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

//@Data
@TableName(value = "seven_goods")
public class GoodEntity implements Serializable {
    private Long id;
    private String goodsName;
    private Long goodsId;
    private Long brandId;
    private Long supplierId;
    private Long cateId;
    private Integer num;
    private Integer numWarnThreshold;
    private Integer hasSku;
    private Long shopPrice;
    private Long costPrice;
    private Long minSalesPrice;
    private Long maxSalesPrice;
    private String goodsCode;
    private String tags;
    private Integer status;
    private Integer stockStatus;
    private Integer isDeleted;
    private Date gmtCreate;
    private Date gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getCateId() {
        return cateId;
    }

    public void setCateId(Long cateId) {
        this.cateId = cateId;
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

    public Integer getHasSku() {
        return hasSku;
    }

    public void setHasSku(Integer hasSku) {
        this.hasSku = hasSku;
    }

    public Long getShopPrice() {
        return shopPrice;
    }

    public void setShopPrice(Long shopPrice) {
        this.shopPrice = shopPrice;
    }

    public Long getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Long costPrice) {
        this.costPrice = costPrice;
    }

    public Long getMinSalesPrice() {
        return minSalesPrice;
    }

    public void setMinSalesPrice(Long minSalesPrice) {
        this.minSalesPrice = minSalesPrice;
    }

    public Long getMaxSalesPrice() {
        return maxSalesPrice;
    }

    public void setMaxSalesPrice(Long maxSalesPrice) {
        this.maxSalesPrice = maxSalesPrice;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(Integer stockStatus) {
        this.stockStatus = stockStatus;
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
