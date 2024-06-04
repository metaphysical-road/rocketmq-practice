package com.rocketmq.cloud.youxia.dto;

import lombok.Data;
import java.io.Serializable;

//@Data
public class SevenAccountInfoDto implements Serializable {
    private Long id;
    private String accountName;
    private Long amount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
