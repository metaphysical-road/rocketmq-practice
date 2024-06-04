package com.rocketmq.cloud.youxia;

import com.rocketmq.cloud.youxia.pool.ConsumerCenterPool;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DynamicJob implements SimpleJob {

    @Autowired
    private DynamicConfig dynamicConfig;
    @Autowired
    private ConsumerCenterPool consumerCenterPool;
    private volatile Boolean isInitBasicCapacity = false;

    @Override
    public void execute(ShardingContext shardingContext) {
        String topicName=dynamicConfig.getTopicName();
        if(topicName.contains(";")){
            String[] s=topicName.split(";");
            for(String item:s){
                init(item);
            }
        }else{
            init(topicName);
        }
    }

    private void init(String item) {
        //初始化基础消费能力
        if (!isInitBasicCapacity) {
            //模拟多个进程，向指定消息主题生产消息的业务场景，生产者组名称一致，instanceName和clientIP不一致。
            //初始化三个DefaultMQProducer对象，对应三个MQClientInstance
            consumerCenterPool.updateDefaultMQConsumerInfo(dynamicConfig.getMappingRelation(),
                    dynamicConfig.getNameServerAddress(), "0", item);
            isInitBasicCapacity = true;
        }
        //如果开启扩容
        if (dynamicConfig.getDilatation().equals("true")) {
            //先执行扩容，如果配置中心中的Producer已经存在缓存中，则不执行，否则新增一个Consumer
            consumerCenterPool.updateDefaultMQConsumerInfo(dynamicConfig.getMappingRelationExt(),
                    dynamicConfig.getNameServerAddress(), "0", item);
        }
        //如果开启缩容
        if (dynamicConfig.getShrinkage().equals("true")) {
            //再执行缩容，如果配置中心中的Consumer已经不存在缓存中，则不执行，否则删除这个Consumer
            consumerCenterPool.updateDefaultMQConsumerInfo(dynamicConfig.getMappingRelationReduce(),
                    dynamicConfig.getNameServerAddress(), "1", item);
        }
    }
}
