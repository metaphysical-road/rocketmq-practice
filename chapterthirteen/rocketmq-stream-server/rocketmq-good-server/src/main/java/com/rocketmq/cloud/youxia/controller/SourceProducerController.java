package com.rocketmq.cloud.youxia.controller;

import com.alibaba.cloud.dubbo.util.JSONUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rocketmq.cloud.youxia.config.GoodConfig;
import com.rocketmq.cloud.youxia.entity.GoodEntity;
import com.rocketmq.cloud.youxia.mapper.GoodMapper;
import com.rocketmq.cloud.youxia.util.ProducerUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping(value = "/good")
public class SourceProducerController {

    @Autowired
    private GoodMapper goodMapper;
    @Autowired
    private GoodConfig goodConfig;

    @PostMapping(value = "/data")
    public String producer() {
        QueryWrapper<GoodEntity> queryWrapper = new QueryWrapper<>();
        List<GoodEntity> result = goodMapper.selectList(queryWrapper);
        StringBuilder builder = new StringBuilder();
        if (CollectionUtils.isNotEmpty(result)) {
            for (GoodEntity item : result) {
                JSONUtils jsonUtils = new JSONUtils();
                String body = jsonUtils.toJSON(item);
                if (StringUtils.isEmpty(builder.toString())) {
                    builder.append(body);
                } else {
                    builder.append("-").append(body);
                }
            }
            try {
                DefaultMQProducer defaultMQProducer = ProducerUtil.getInstance().getDefaultMQProducer(
                        goodConfig
                );
                Message message = new Message(goodConfig.getSourceTopicName(),
                        (builder.toString()).getBytes(RemotingHelper.DEFAULT_CHARSET));
                defaultMQProducer.send(message);
            } catch (MQClientException e) {
                System.out.println(e.getMessage());
            } catch (UnsupportedEncodingException e1) {
                System.out.println(e1.getMessage());
            } catch (RemotingException e2) {
                System.out.println(e2.getMessage());
            } catch (InterruptedException e3) {
                System.out.println(e3.getMessage());
            } catch (MQBrokerException e4) {
                System.out.println(e4.getMessage());
            }
        }
        return "success";
    }
}
