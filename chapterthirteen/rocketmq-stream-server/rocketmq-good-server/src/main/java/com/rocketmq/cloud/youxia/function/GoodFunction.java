package com.rocketmq.cloud.youxia.function;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GoodFunction {
    private Long id;
    private String goodId;
    private Long saleNum;
    private static Map<String,Long> realSaleNum=new ConcurrentHashMap<>();

    public GoodFunction() {
    }

    public GoodFunction(Long id,String goodId,Long saleNum){
        this.id=id;
        this.goodId=goodId;
        this.saleNum=saleNum;
        realSaleNum.put(goodId,saleNum);
    }

    public Long getId() {
        return id;
    }

    public Number getSaleNum() {
        return saleNum;
    }

    public String getGoodId() {
        return goodId;
    }

    public void setGoodId(String goodId) {
        this.goodId = goodId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSaleNum(Long saleNum) {
        this.saleNum = saleNum;
    }

    @Override
    public String toString() {
        return "GoodFunction{" +
                "id='" + id + '\'' +
                ", goodId=" + goodId +
                '}';
    }

    public static Number getSaleNum(String s) {
        return realSaleNum.get(s);
    }
}
