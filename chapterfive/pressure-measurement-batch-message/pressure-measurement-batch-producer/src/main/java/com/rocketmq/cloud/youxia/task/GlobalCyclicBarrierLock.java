package com.rocketmq.cloud.youxia.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class GlobalCyclicBarrierLock {

    private Lock globalLock=new ReentrantLock();

    public Lock getGlobalLock() {
        return globalLock;
    }

    @Autowired
    private StatisticsTask statisticsTask;

    private  CyclicBarrier globalCyclic=new CyclicBarrier(2, new Runnable() {
        @Override
        public void run() {
            //两线程执行完毕，之后开始统计，做到线程的步调一致
            statisticsTask.statistics();
        }
    });

    public CyclicBarrier getGlobalCyclic() {
        return globalCyclic;
    }
}
