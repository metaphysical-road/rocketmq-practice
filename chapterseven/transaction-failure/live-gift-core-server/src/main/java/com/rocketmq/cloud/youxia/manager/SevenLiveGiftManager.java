package com.rocketmq.cloud.youxia.manager;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rocketmq.cloud.youxia.entity.SevenLiveGiftEntity;
import com.rocketmq.cloud.youxia.mapper.SevenLiveGiftMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
public class SevenLiveGiftManager {

    @Autowired
    private SevenLiveGiftMapper sevenLiveGiftMapper;

    //添加本地事务
    @Transactional(rollbackFor = Exception.class)
    public Integer update(SevenLiveGiftEntity sevenLiveGift) {
        QueryWrapper<SevenLiveGiftEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("id",sevenLiveGift.getId());
        SevenLiveGiftEntity update=new SevenLiveGiftEntity();
        update.setPrice(sevenLiveGift.getPrice());
        update.setNum(sevenLiveGift.getNum());
        update.setGmt_modified(sevenLiveGift.getGmt_modified());
        return sevenLiveGiftMapper.update(update,queryWrapper);
    }

    public SevenLiveGiftEntity selectBySevenLiveGiftEntity(SevenLiveGiftEntity sevenLiveGiftEntity){
        QueryWrapper<SevenLiveGiftEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",sevenLiveGiftEntity.getId());
        return sevenLiveGiftMapper.selectOne(queryWrapper);
    }

    public List<SevenLiveGiftEntity> selectAll(){
        Wrapper<SevenLiveGiftEntity> queryWrapper = new QueryWrapper<>();
        return sevenLiveGiftMapper.selectList(queryWrapper);
    }

    public void insertData(SevenLiveGiftEntity sevenLiveGift){
        sevenLiveGiftMapper.insert(sevenLiveGift);
    }

    public Integer count(){
        Wrapper<SevenLiveGiftEntity> queryWrapper = new QueryWrapper<>();
        return sevenLiveGiftMapper.selectCount(queryWrapper);
    }
}
