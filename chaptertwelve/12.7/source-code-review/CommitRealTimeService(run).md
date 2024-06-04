启动线程，并执行CommitRealTimeService类的run()方法，加载堆外内存中的消息到存储引擎的Page Cache中，具体代码如下所示：

```java
class CommitRealTimeService extends FlushCommitLogService {
    ...
    @Override
    public void run() {
        CommitLog.log.info(this.getServiceName() + " service started");
        while (!this.isStopped()) {
            //①获取执行线程CommitRealTimeService类的周期，默认为200ms
            int interval = CommitLog.this.defaultMessageStore.
                getMessageStoreConfig().getCommitIntervalCommitLog();
            //②获取提交消息到FileChannel的Page Cache的最小Page数，默认为4
            int commitDataLeastPages =CommitLog.this.defaultMessageStore.
                getMessageStoreConfig().getCommitCommitLogLeastPages();
            //③获取提交消息到FileChannel的最大耗时，默认为200ms
            int commitDataThoroughInterval=CommitLog.this.
                 defaultMessageStore.getMessageStoreConfig().
                     getCommitCommitLogThoroughInterval();
            long begin = System.currentTimeMillis();
            //④如果当前系统时间大于和等于“上次提交消息的时间和提交消息到
              FileChannel的最大耗时之和”，则设置提交消息到FileChannel的Page 
                 Cache的最小Page数为0
            if (begin >= (this.lastCommitTimestamp + 
                commitDataThoroughInterval)) {
                this.lastCommitTimestamp = begin;
                commitDataLeastPages = 0;
            }
            try {
                //⑤调用MappedFileQueue类的commit()方法，提交消息到
                    FileChannel
                boolean result = CommitLog.this.
                    mappedFileQueue.commit(commitDataLeastPages);
                long end = System.currentTimeMillis();
                //⑥如果提交消息失败
                if (!result) {
                    //⑦则设置“上次提交消息的时间”为当前系统时间
                    this.lastCommitTimestamp = end;
                    //⑧则唤醒线程flushCommitLogService 
                    flushCommitLogService.wakeup();
                }
                if (end - begin > 500) {
                    log.info("Commit data to file costs {} ms", end - begin);
                }
                //⑨当前线程等待200ms
                this.waitForRunning(interval);
            } catch (Throwable e) {
                CommitLog.log.error(this.getServiceName() + " service has 
                    exception. ", e);
            }
        }
        //⑩如果提交消息到FileChannel失败，则重试（重试次数的阈值默认为10次）
        boolean result = false;
        for (int i = 0; i < RETRY_TIMES_OVER && !result; i++) {
            result = CommitLog.this.mappedFileQueue.commit(0);
            CommitLog.log.info(this.getServiceName() + " service shutdown, 
              retry " + (i + 1) + " times " + (result ? "OK" : "Not OK"));
        }
        CommitLog.log.info(this.getServiceName() + " service end");
    }
}
```

