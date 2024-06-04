package com.rocketmq.cloud.youxia;

import com.rocketmq.cloud.youxia.pool.ProducerCenterPool;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ProducerStatusJob implements SimpleJob {
    @Autowired
    private ProducerCenterPool producerCenterPool;
    @Override
    public void execute(ShardingContext shardingContext) {
        Map<String, AtomicBoolean> status= producerCenterPool.getMqProducerStatus();
        Iterator<String> iterator=status.keySet().iterator();
        StringBuilder result=new StringBuilder();
        while (iterator.hasNext()){
            String key=iterator.next();
            AtomicBoolean value=status.get(key);
            if(StringUtils.isEmpty(result.toString())){
                result.append("生产者实例列表运行状态为").append(":");
                result.append(key).append(":");
                result.append(value);
            }else{
                result.append(":").append(key).append(":").append(value);
            }
        }
        System.out.println(result.toString());
    }
}
