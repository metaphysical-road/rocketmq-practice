package com.rocketmq.cloud.youxia.service;

import com.rocketmq.cloud.youxia.common.CommonCode;
import com.rocketmq.cloud.youxia.common.Result;
import com.rocketmq.cloud.youxia.dto.SevenPayDto;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@DubboService(group = "rocketmq-practice",version = "1.0.0")
public class PayService implements SevenPayService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Result<SevenPayDto> pay(Long orderId) {
        Result<SevenPayDto> result = new Result<SevenPayDto>(CommonCode.SUCCESS.getCode(), "成功");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HashMap<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        HttpEntity<HashMap<String, Object>> request = new HttpEntity(map, httpHeaders);
        Map<String, Object> responseResult = restTemplate.postForObject("http://third-channel-api/pay/onlinePay",
                request, Map.class);
        //调用第三方支付服务，完成支付。
        result.setErrorCode(Long.valueOf(responseResult.
                get("code").toString()));
        result.setMsg(responseResult.get("message").toString());
        SevenPayDto sevenPayDto = new SevenPayDto();
        sevenPayDto.setPayId(0L);
        sevenPayDto.setOrderId(0L);
        sevenPayDto.setUuid(0L);
        result.setData(sevenPayDto);
        return result;
    }
}
