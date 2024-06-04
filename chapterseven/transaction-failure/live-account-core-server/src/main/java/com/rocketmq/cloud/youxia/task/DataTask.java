package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.AccountConfig;
import com.rocketmq.cloud.youxia.entity.SevenAccountInfoEntity;
import com.rocketmq.cloud.youxia.manager.SevenAccountInfoManager;
import com.rocketmq.cloud.youxia.service.DistributedService;
import org.apache.commons.lang3.RandomUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.Random;
import java.util.concurrent.atomic.LongAdder;

@EnableScheduling
@Component
public class DataTask {

    @Autowired
    private SevenAccountInfoManager sevenAccountInfoManager;

    @Autowired
    private AccountConfig accountConfig;

    private volatile LongAdder longAdder=new LongAdder();

    @DubboReference(version = "1.0.0",group = "rocketmq-practice")
    private DistributedService distributedService;

    //定时的造数据
    @Scheduled(fixedRate = 60000)
    public void makeAccountData() {
        if (accountConfig.getOpenInsertData().equals("true")) {
            Integer accountNum = accountConfig.getAccountNum();
            Integer dbnum = sevenAccountInfoManager.count();
            if (accountNum > 0) {
                while (longAdder.intValue() + dbnum.intValue() < accountNum) {
                    Integer add = accountNum - longAdder.intValue() - dbnum.intValue();
                    for (int i = 0; i < add; i++) {
                        SevenAccountInfoEntity sevenAccountInfoEntity = new SevenAccountInfoEntity();
                        sevenAccountInfoEntity.setId(distributedService.nextId());
                        sevenAccountInfoEntity.setAccountName("测试账户" + RandomUtils.nextLong(0, 100000000));
                        sevenAccountInfoEntity.setAmount(RandomUtils.nextLong(0, 1000));
                        sevenAccountInfoEntity.setGmt_create(new Date(System.currentTimeMillis()));
                        sevenAccountInfoEntity.setGmt_modified(new Date(System.currentTimeMillis()));
                        sevenAccountInfoEntity.setIsDeleted(0);
                        sevenAccountInfoManager.insertData(sevenAccountInfoEntity);
                        longAdder.increment();
                    }
                }
            }
        }
    }
}
