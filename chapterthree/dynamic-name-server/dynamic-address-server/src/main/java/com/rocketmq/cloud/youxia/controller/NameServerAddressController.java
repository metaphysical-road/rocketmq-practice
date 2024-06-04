package com.rocketmq.cloud.youxia.controller;

import com.rocketmq.cloud.youxia.config.NameServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/rocketmq")
public class NameServerAddressController {

    @Autowired
    private NameServerConfig nameServerConfig;

    @GetMapping(value = "/nsaddr")
    @ResponseBody
    public String getNameServerAddress(HttpServletRequest request) {
        String url = request.getRemoteHost()+":"+request.getRemotePort();
        String nameServerAddress = nameServerConfig.getNameServerAddress();
        System.out.println(url + "访问地址服务，获取Name ServerIP地址信息:" + nameServerAddress);
        return nameServerAddress;
    }
}
