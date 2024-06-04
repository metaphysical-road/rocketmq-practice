package com.rocketmq.cloud.youxia.task;

import com.rocketmq.cloud.youxia.config.ProducerConfig;
import com.rocketmq.cloud.youxia.service.DistributedService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.list.SynchronizedList;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@EnableScheduling
public class SimulateOrderManager {
    List<Long> threadSafelists = SynchronizedList.decorate(new ArrayList());

    @DubboReference(version = "1.0.0", group = "rocketmq-practice")
    private DistributedService distributedService;

    private volatile AtomicBoolean isInitialize = new AtomicBoolean(false);

    @Autowired
    ProducerConfig producerConfig;

    @Scheduled(fixedRate = 1000)
    public void generateOrder() {
        Integer cacheSize = producerConfig.getOrderPoolSize();
        if (!isInitialize.equals(true)) {
            for (int i = 0; i < cacheSize; i++) {
                threadSafelists.add(distributedService.nextId());
            }
            isInitialize.set(true);
        }
        if (threadSafelists.size() < cacheSize) {
            int addNewSize = cacheSize - threadSafelists.size();
            for (int i = 0; i < addNewSize; i++) {
                threadSafelists.add(distributedService.nextId());
            }
        }
//        if (CollectionUtils.isNotEmpty(threadSafelists)) {
//            StringBuilder newStringBuilder = new StringBuilder();
//            for (Long s : threadSafelists) {
//                if (StringUtils.isEmpty(newStringBuilder.toString())) {
//                    newStringBuilder.append(s + ":");
//                } else {
//                    newStringBuilder.append(":" + s);
//                }
//            }
//            System.out.println("模拟的订单ID的列表：" + newStringBuilder.toString());
//        }
    }

    public Long randomId() {
        if (CollectionUtils.isNotEmpty(threadSafelists)) {
            Integer size = threadSafelists.size();
            Long index = RandomUtils.nextLong(0, size - 1);
            return threadSafelists.get(index.intValue());
        }
        return 0L;
    }
}
