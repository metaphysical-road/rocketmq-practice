package com.rocketmq.cloud.youxia.service;

import com.rocketmq.cloud.youxia.dto.SevenAccountInfoDto;
import java.util.List;

public interface SevenAccountInfoService {
    List<SevenAccountInfoDto> selectAll();
}