package com.rocketmq.cloud.youxia.enpoint;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/message")
public class ProducerController {
    @Autowired
    private StreamBridge streamBridge;

    @GetMapping("/send")
    public String sendMessage() {
        boolean result = streamBridge.send("source1-out-0", "test" + RandomUtils.nextLong(0, 1000000000));
        if (result == true) {
            return "success";
        } else {
            return "fail";
        }
    }
}
