package com.rocketmq.cloud.youxia.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

//@Data
@TableName(value = "seven_supplier_settlement")
public class SupplierSettlementEntity implements Serializable {
    private Long id;
    private Long supplier_id;
    private Integer accountType;
    private String  accountName;
    private String bankOfDepositAccount;
    private String bankOfDepositName;
    private String valueAddedTaxRate;
    private String settlementRate;
    private Integer isDeleted;
    private Date gmtCreate;
    private Date gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(Long supplier_id) {
        this.supplier_id = supplier_id;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getBankOfDepositAccount() {
        return bankOfDepositAccount;
    }

    public void setBankOfDepositAccount(String bankOfDepositAccount) {
        this.bankOfDepositAccount = bankOfDepositAccount;
    }

    public String getBankOfDepositName() {
        return bankOfDepositName;
    }

    public void setBankOfDepositName(String bankOfDepositName) {
        this.bankOfDepositName = bankOfDepositName;
    }

    public String getValueAddedTaxRate() {
        return valueAddedTaxRate;
    }

    public void setValueAddedTaxRate(String valueAddedTaxRate) {
        this.valueAddedTaxRate = valueAddedTaxRate;
    }

    public String getSettlementRate() {
        return settlementRate;
    }

    public void setSettlementRate(String settlementRate) {
        this.settlementRate = settlementRate;
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
