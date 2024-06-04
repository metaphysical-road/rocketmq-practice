package com.rocketmq.cloud.youxia.task;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;

@Component
public class StatisticsPool {

    private  final Map<String, Map<String,String>> timeConsuming= new ConcurrentHashMap<>();

    public static final String constantFlag="batch-single";

    public volatile AtomicBoolean isIncrement=new AtomicBoolean(false);

    public AtomicBoolean getIsIncrement() {
        return isIncrement;
    }

    private volatile LongAdder longAdder=new LongAdder();

    public synchronized void increment(){
        longAdder.increment();
    }

    public synchronized void decrement(){
        longAdder.decrement();
    }

    public LongAdder getLongAdder() {
        return longAdder;
    }

    public Map<String, Map<String, String>> getTimeConsuming() {
        return timeConsuming;
    }

    public synchronized void addTimeConsuming(final String key, final String json,
                                              final StatisticsEnum type) {
        //如果包含唯一key
        if (timeConsuming.containsKey(key)) {
            Map<String, String> item = timeConsuming.get(key);
            item.put(type.getKey(),json);
        } else {
            Map<String, String> item=new HashMap<>();
            item.put(type.getKey(),json);
            timeConsuming.put(key,item);
        }
    }
}
