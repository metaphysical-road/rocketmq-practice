package com.rocketmq.cloud.youxia.service;
import com.rocketmq.cloud.youxia.source.ProduceMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class SenderService {

	@Autowired
	private ProduceMessageSource source;

	public void send(String msg) {
		Message message=MessageBuilder.withPayload(msg).build();
		System.out.println("生产消息："+message.toString());
		source.output1().send(message);
	}
}
