package com.rocketmq.cloud.youxia.task;

import com.google.common.base.Charsets;
import com.rocketmq.cloud.youxia.config.ProducerConfig;
import com.rocketmq.cloud.youxia.listener.LocalTransactionListener;
import com.rocketmq.cloud.youxia.trace.TraceUtils;
import io.opentracing.Tracer;
import org.apache.commons.lang3.RandomUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.trace.hook.EndTransactionOpenTracingHookImpl;
import org.apache.rocketmq.client.trace.hook.SendMessageOpenTracingHookImpl;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicBoolean;

@EnableScheduling
@Component
public class JaegerProducerTask {
    @Autowired
    private ProducerConfig producerConfig;

    @Autowired
    private TraceUtils traceUtils;

    @Autowired
    private LocalTransactionListener localTransactionListener;

    private volatile AtomicBoolean startFlag= new AtomicBoolean(false);

    @Scheduled(fixedRate = 1000)
    public void producerJaegerTraceMessage() throws UnsupportedEncodingException, MQClientException, RemotingException
        , MQBrokerException ,InterruptedException {
        if (startFlag.compareAndSet(false, true)) {
            Tracer tracer = traceUtils.initTracer();
            if (producerConfig.getIsTransaction().equals("false")) {
                //①初始化一个Producer客户端
                DefaultMQProducer producer = new DefaultMQProducer(producerConfig.getProducerGroup());
                //②注册一个生产者Hook，并绑定一个实例化的Tracer对象
                producer.getDefaultMQProducerImpl().registerSendMessageHook(new
                        SendMessageOpenTracingHookImpl(tracer));
                producer.setNamesrvAddr(producerConfig.getNamesrvAddr());
                producer.setClientIP(producerConfig.getCientIp());
                producer.setInstanceName(producer.getInstanceName() + RandomUtils.nextLong(0, 500000000));
                producer.setProducerGroup(producerConfig.getProducerGroup());
                producer.start();
                for (int i = 0; i < 20000; i++) {
                    Message msg = new Message(producerConfig.getTopic(),
                            ("this is a jaeger trace message" + RandomUtils.nextLong(1, 20000000)).
                                    getBytes(RemotingHelper.DEFAULT_CHARSET));
                    producer.send(msg);
                }
                startFlag.set(true);
            } else {
                //①初始化一个Producer客户端
                TransactionMQProducer producer = new
                        TransactionMQProducer(producerConfig.getProducerGroup());
                //②注册一个生产事务消息类型的Hook，并绑定一个实例化的Tracer对象
                producer.getDefaultMQProducerImpl().registerSendMessageHook(new
                        SendMessageOpenTracingHookImpl(tracer));
                //②注册一个结束事务消息类型的Hook，并绑定一个实例化的Tracer对象
                producer.getDefaultMQProducerImpl().registerEndTransactionHook(new
                        EndTransactionOpenTracingHookImpl(tracer));
                producer.setNamesrvAddr(producerConfig.getNamesrvAddr());
                producer.setClientIP(producerConfig.getCientIp());
                producer.setProducerGroup(producerConfig.getProducerGroup());
                producer.setTransactionListener(localTransactionListener);
                producer.start();
                for (int i = 0; i < 20000; i++) {
                    Message msg = new Message(producerConfig.getTopic(),
                            ("this is a jaeger transaction trace message" + RandomUtils.nextLong(1, 20000000)).
                                    getBytes(RemotingHelper.DEFAULT_CHARSET));
                    String content = new String(msg.getBody(), Charsets.UTF_8);
                    producer.sendMessageInTransaction(msg,content);
                }
                startFlag.set(true);
            }
        }
    }
}
