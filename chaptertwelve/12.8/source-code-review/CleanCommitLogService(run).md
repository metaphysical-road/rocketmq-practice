在DefaultMessageStore类启动的过程中，通过添加存储引擎定时器，并启动定时任务CleanCommitLogService类，具体代码如下：

```java
//①用JDK的Executors类，初始化一个定时线程池ScheduledExecutorService
private final ScheduledExecutorService scheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor(new 
            ThreadFactoryImpl("StoreScheduledThread"));
//②用DefaultMessageStore类的addScheduleTask()方法添加定时器
private void addScheduleTask() {
    this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
        @Override
        public void run() {
            //③用定时线程池ScheduledExecutorService执行
              DefaultMessageStore类的cleanFilesPeriodically()方法 
            DefaultMessageStore.this.cleanFilesPeriodically();
        }
    }, 1000 * 60, this.messageStoreConfig.getCleanResourceInterval(), 
    TimeUnit.MILLISECONDS);
}

//④启动定时任务CleanCommitLogService类，并执行run()方法
private void cleanFilesPeriodically() {
    this.cleanCommitLogService.run();
}

class CleanCommitLogService {
    ...
    public void run() {
        try {
        //⑤执行DefaultMessageStore类的deleteExpiredFiles()方法，删除过期的
           CommitLog文件
            this.deleteExpiredFiles();
            this.redeleteHangedFile();
        } catch (Throwable e) {
            DefaultMessageStore.log.warn(this.
                getServiceName() + " service has exception. ", e);
        }
     }
}

```

