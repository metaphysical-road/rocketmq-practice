package com.rocketmq.cloud.youxia.task;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Iterator;
import java.util.Map;
@Component
public class StatisticsTask {

    @Autowired
    private StatisticsPool statisticsPool;

    public synchronized void statistics() {
        Map<String, Map<String, String>> stringMap = statisticsPool.getTimeConsuming();
        Iterator<String> keyIterator = stringMap.keySet().iterator();
        StringBuilder sb = new StringBuilder();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            Map<String, String> value = stringMap.get(key);
            Iterator<String> jsonKeyIterator = value.keySet().iterator();
            while (jsonKeyIterator.hasNext()) {
                String jsonKey = jsonKeyIterator.next();
                String jsonValue = value.get(jsonKey);
                if (StringUtils.isEmpty(sb.toString())) {
                    sb.append("statistics:").append(jsonValue).append("\n");
                } else {
                    sb.append(jsonValue);
                }
            }
        }
        System.out.println(sb.toString());
    }
}
