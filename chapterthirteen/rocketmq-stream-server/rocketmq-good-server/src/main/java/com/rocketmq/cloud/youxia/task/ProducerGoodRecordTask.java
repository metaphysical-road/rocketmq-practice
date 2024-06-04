package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.GoodConfig;
import com.rocketmq.cloud.youxia.entity.GoodEntity;
import com.rocketmq.cloud.youxia.mapper.GoodMapper;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ProducerGoodRecordTask {

    @Autowired
    private GoodMapper goodMapper;

    @Autowired
    private GoodConfig goodConfig;

    //构造测试数据
    @Scheduled(fixedRate = 1000)
    public void packageTask() {
        if(goodConfig.getIsOpenPackageData().equals("true")) {
            //构造100万条数据
            for(int i=0;i<100;i++){
                GoodEntity batch=new GoodEntity();
                batch.setId(RandomUtils.nextLong(1,1000000000));
                batch.setGoodId(RandomUtils.nextLong(1,1000000000)+"good");
                batch.setSaleNum(RandomUtils.nextLong(1,1000000000));
                goodMapper.insert(batch);
                try {
                    Thread.sleep(100);
                }catch (InterruptedException e){
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
