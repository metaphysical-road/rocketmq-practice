package com.rocketmq.cloud.youxia.service;
import com.rocketmq.cloud.youxia.dto.SevenAccountInfoDto;
import com.rocketmq.cloud.youxia.entity.SevenAccountInfoEntity;
import com.rocketmq.cloud.youxia.manager.SevenAccountInfoManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@DubboService(version = "1.0.0",group = "rocketmq-practice")
public class SevenAccountInfoServiceImpl implements SevenAccountInfoService {

    @Autowired
    private SevenAccountInfoManager sevenAccountInfoManager;

    @Override
    public List<SevenAccountInfoDto> selectAll() {
        List<SevenAccountInfoDto> result = new CopyOnWriteArrayList<>();
        List<SevenAccountInfoEntity> queryAll = sevenAccountInfoManager.selectAll();
        if (CollectionUtils.isNotEmpty(queryAll)) {
            for (SevenAccountInfoEntity sevenAccountInfoEntity : queryAll) {
                SevenAccountInfoDto item = new SevenAccountInfoDto();
                item.setId(sevenAccountInfoEntity.getId());
                item.setAccountName(sevenAccountInfoEntity.getAccountName());
                item.setAmount(sevenAccountInfoEntity.getAmount());
                result.add(item);
            }
        }
        return result;
    }
}
