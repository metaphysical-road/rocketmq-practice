package com.rocketmq.cloud.youxia.listener;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

@Service
public class ReceiveMessageListener {
	@StreamListener("input1")
	public void receiveInput1(String receiveMsg) {
		System.out.println("input1 receive: " + receiveMsg);
	}
}
