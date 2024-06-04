package com.rocketmq.cloud.youxia;

import com.rocketmq.cloud.youxia.pool.ProducerCenterPool;
import com.rocketmq.cloud.youxia.task.SendMessageTask;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 模拟高并发流量的生产消息
 */
@Component
public class ProducerMessageJob implements SimpleJob {
    @Autowired
    private DynamicConfig dynamicConfig;
    @Autowired
    private ProducerCenterPool producerCenterPool;

    //定时Job，每次批量生产消息，并将生成生产消息的任务，下方给基础能力，
    @Override
    public void execute(ShardingContext shardingContext) {
        String topicName = dynamicConfig.getTopicName();
        if(StringUtils.isNotEmpty(topicName)){
            if(topicName.contains(";")){
                sendBatchTopicMessage(topicName);
            }else{
                sendMessage(topicName);
            }
        }else{
            System.out.println("topic name is null!");
        }
    }

    private void sendBatchTopicMessage(String topicName) {
        String [] topicArray= topicName.split(";");
        for(String name:topicArray){
            sendMessage(name);
        }
    }

    private void sendMessage(String topic) {
        Integer num = dynamicConfig.getBatchNum();
        List<SendMessageTask> sendMessageTasks = new CopyOnWriteArrayList<>();
        List<DefaultMQProducer> defaultMQProducerList = producerCenterPool.getDefaultMQProducer(topic);
        if (CollectionUtils.isNotEmpty(defaultMQProducerList)) {
            for (int i = 0; i < num; i++) {
                try {
                    Message msg = new Message(topic,
                            "order454545",
                            "454545",
                            ("This is a testA message" + RandomUtils.nextLong(1, 20000000)).getBytes(RemotingHelper.DEFAULT_CHARSET));
                    SendMessageTask task = new SendMessageTask();
                    task.setMessage(msg);
                    sendMessageTasks.add(task);
                } catch (UnsupportedEncodingException e) {
                    System.out.println(e.getMessage());
                }
            }
            for (SendMessageTask sendMessageTask : sendMessageTasks) {
                //随机算法更公平
                int random = RandomUtils.nextInt(0, defaultMQProducerList.size() - 1);
                DefaultMQProducer defaultMQProducer = defaultMQProducerList.get(random);
                try {
                    defaultMQProducer.send(sendMessageTask.getMessage());
                } catch (MQClientException mqClientException) {
                    System.out.println(mqClientException.getMessage());
                } catch (RemotingException remotingException) {
                    System.out.println(remotingException.getMessage());
                } catch (MQBrokerException mqBrokerException) {
                    System.out.println(mqBrokerException.getMessage());
                } catch (InterruptedException interruptedException) {
                    System.out.println(interruptedException.getMessage());
                }
            }
        }
    }
}
