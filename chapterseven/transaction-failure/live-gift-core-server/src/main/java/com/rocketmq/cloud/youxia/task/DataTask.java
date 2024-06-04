package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.LiveGiftConfig;
import com.rocketmq.cloud.youxia.dto.SevenAccountInfoDto;
import com.rocketmq.cloud.youxia.entity.SevenLiveGiftEntity;
import com.rocketmq.cloud.youxia.manager.SevenLiveGiftManager;
import com.rocketmq.cloud.youxia.service.DistributedService;
import com.rocketmq.cloud.youxia.service.SevenAccountInfoService;
import org.apache.commons.lang3.RandomUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

@EnableScheduling
@Component
public class DataTask {

    @Autowired
    private SevenLiveGiftManager sevenLiveGiftManager;

    @DubboReference(version = "1.0.0",group = "rocketmq-practice")
    private SevenAccountInfoService sevenAccountInfoService;

    @DubboReference(version = "1.0.0",group = "rocketmq-practice")
    private DistributedService distributedService;

    @Autowired
    private LiveGiftConfig liveGiftConfig;

    private volatile LongAdder adder=new LongAdder();

    //定时的造数据
    @Scheduled(fixedRate = 1000)
    public void insertData() {
        if (liveGiftConfig.getOpenInsertData().equals("true")) {
            List<SevenAccountInfoDto> queryResult = sevenAccountInfoService.selectAll();
            Integer dbnum = sevenLiveGiftManager.count();
            while (adder.intValue() + dbnum < liveGiftConfig.getGiftNum()) {
                Integer add = liveGiftConfig.getGiftNum() - adder.intValue() - dbnum;
                for (int i = 0; i < add; i++) {
                    Integer index = RandomUtils.nextInt(0, queryResult.size());
                    SevenLiveGiftEntity item = new SevenLiveGiftEntity();
                    item.setId(distributedService.nextId());
                    item.setGiftName("测试礼物" + RandomUtils.nextLong(0, 1434544545));
                    item.setNum(RandomUtils.nextInt(0, 2323));
                    item.setIsDeleted(0);
                    item.setPrice(RandomUtils.nextLong(0, 10));
                    item.setGmt_create(new Date(System.currentTimeMillis()));
                    item.setGmt_modified(new Date(System.currentTimeMillis()));
                    item.setAccountId(queryResult.get(index).getId());
                    sevenLiveGiftManager.insertData(item);
                    adder.increment();
                }
            }
        }
    }
}
