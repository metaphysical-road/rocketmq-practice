package com.rocketmq.cloud.youxia.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rocketmq.cloud.youxia.entity.SevenLogEntity;
import com.rocketmq.cloud.youxia.mapper.SevenLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SevenLogManager {
    @Autowired
    private SevenLogMapper sevenLogMapper;

    public Integer insertLog(SevenLogEntity item) {
        return sevenLogMapper.insert(item);
    }

    public SevenLogEntity selectLog(SevenLogEntity query){
        QueryWrapper<SevenLogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uk",query.getUk());
        return sevenLogMapper.selectOne(queryWrapper);
    }
}
