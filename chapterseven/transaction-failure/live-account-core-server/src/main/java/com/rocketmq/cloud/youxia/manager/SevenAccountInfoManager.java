package com.rocketmq.cloud.youxia.manager;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rocketmq.cloud.youxia.entity.SevenAccountInfoEntity;
import com.rocketmq.cloud.youxia.mapper.SevenAccountInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class SevenAccountInfoManager {

    @Autowired
    private SevenAccountInfoMapper sevenAccountInfoMapper;

    //造数据，忽略性能
    public List<SevenAccountInfoEntity>  selectAll() {
        Wrapper<SevenAccountInfoEntity> queryWrapper = new QueryWrapper<>();
        List<SevenAccountInfoEntity> all = sevenAccountInfoMapper.selectList(queryWrapper);
        return all;
    }

    public Integer update(SevenAccountInfoEntity sevenAccountInfoEntity){
        return sevenAccountInfoMapper.updateById(sevenAccountInfoEntity);
    }

    public SevenAccountInfoEntity select(SevenAccountInfoEntity sevenAccountInfoEntity){
        QueryWrapper<SevenAccountInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",sevenAccountInfoEntity.getId());
        return sevenAccountInfoMapper.selectOne(queryWrapper);
    }

    public void insertData(SevenAccountInfoEntity sevenAccountInfoEntity){
        sevenAccountInfoMapper.insert(sevenAccountInfoEntity);
    }

    public Integer count(){
        Wrapper<SevenAccountInfoEntity> queryWrapper = new QueryWrapper<>();
        return sevenAccountInfoMapper.selectCount(queryWrapper);
    }
}
