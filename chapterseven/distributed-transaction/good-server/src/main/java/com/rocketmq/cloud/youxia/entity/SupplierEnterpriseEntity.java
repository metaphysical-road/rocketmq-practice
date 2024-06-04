package com.rocketmq.cloud.youxia.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

//@Data
@TableName(value = "seven_supplier_after_sale")
public class SupplierEnterpriseEntity implements Serializable {
    private Long id;
    private Long supplierId;
    private String businessLicenseLink;
    private String supplierName;
    private String businessLicenseRegNo;
    private String businessLicenseRegAddress;
    private Date businessLicenseStartTime;
    private Date businessLicenseEndTime;
    private Long height;
    private Long width;
    private Integer isDeleted;
    private Date gmtCreate;
    private Date gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getBusinessLicenseLink() {
        return businessLicenseLink;
    }

    public void setBusinessLicenseLink(String businessLicenseLink) {
        this.businessLicenseLink = businessLicenseLink;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getBusinessLicenseRegNo() {
        return businessLicenseRegNo;
    }

    public void setBusinessLicenseRegNo(String businessLicenseRegNo) {
        this.businessLicenseRegNo = businessLicenseRegNo;
    }

    public String getBusinessLicenseRegAddress() {
        return businessLicenseRegAddress;
    }

    public void setBusinessLicenseRegAddress(String businessLicenseRegAddress) {
        this.businessLicenseRegAddress = businessLicenseRegAddress;
    }

    public Date getBusinessLicenseStartTime() {
        return businessLicenseStartTime;
    }

    public void setBusinessLicenseStartTime(Date businessLicenseStartTime) {
        this.businessLicenseStartTime = businessLicenseStartTime;
    }

    public Date getBusinessLicenseEndTime() {
        return businessLicenseEndTime;
    }

    public void setBusinessLicenseEndTime(Date businessLicenseEndTime) {
        this.businessLicenseEndTime = businessLicenseEndTime;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
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
