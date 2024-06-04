package com.rocketmq.cloud.youxia.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

//@Data
@TableName(value = "seven_supplier_after_sale")
public class SupplierAfterSaleEntity implements Serializable {
    private Long id;
    private String supplierId;
    private String refundRegion;
    private String refundAddress;
    private String refundContactName;
    private String refundContactPhone;
    private Integer isDeleted;
    private Date gmtCreate;
    private Date gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getRefundRegion() {
        return refundRegion;
    }

    public void setRefundRegion(String refundRegion) {
        this.refundRegion = refundRegion;
    }

    public String getRefundAddress() {
        return refundAddress;
    }

    public void setRefundAddress(String refundAddress) {
        this.refundAddress = refundAddress;
    }

    public String getRefundContactName() {
        return refundContactName;
    }

    public void setRefundContactName(String refundContactName) {
        this.refundContactName = refundContactName;
    }

    public String getRefundContactPhone() {
        return refundContactPhone;
    }

    public void setRefundContactPhone(String refundContactPhone) {
        this.refundContactPhone = refundContactPhone;
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
