定时任务首次执行时延迟60s，后按照固定周期（默认10s）执行定时任务CleanConsumeQueueService类的run()方法，清理过期的ConsumeQueue文件，具体代码如下：

```java
//①用来记录历史最小的消费位置offset
private long lastPhysicalMinOffset = 0; 
public void run() {
    try {
        //②调用deleteExpiredFiles()方法，执行清理清理过期的ConsumeQueue文件 
              的请求
        this.deleteExpiredFiles();
    } catch (Throwable e) {
    }
}
private void deleteExpiredFiles() {
    //③获取“清理过期消费队列”线程休眠时间，默认为100ms
    int deleteLogicsFilesInterval = DefaultMessageStore.this.
getMessageStoreConfig().getDeleteConsumeQueueFilesInterval();
    //④获取存储引擎中所有内存映射文件中最小的消费位置offset
    long minOffset = DefaultMessageStore.this.commitLog.getMinOffset();
    //⑤对比minOffset 和lastPhysicalMinOffset的大小
    if (minOffset > this.lastPhysicalMinOffset) {
   //⑥如果minOffset 大于lastPhysicalMinOffset，则将minOffset 赋值给
      lastPhysicalMinOffset
        this.lastPhysicalMinOffset = minOffset;
        ConcurrentMap<String, ConcurrentMap<Integer, ConsumeQueue>> tables = 
           DefaultMessageStore.this.consumeQueueTable;
        //⑦遍历存储引擎中的消费队列列表
        for (ConcurrentMap<Integer, ConsumeQueue> maps : tables.values()) 
            {
            for (ConsumeQueue logic : maps.values()) {
                //⑧调用ConsumeQueue 类的deleteExpiredFile()，删除过期的消费
                  队列
                int deleteCount = logic.deleteExpiredFile(minOffset);
                if (deleteCount > 0 && deleteLogicsFilesInterval > 0) {
                    try {
                        //⑨清理过期消费队列”线程休眠
                        Thread.sleep(deleteLogicsFilesInterval);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }
    //⑩调用索引服务IndexService类的deleteExpiredFile(),删除过期的索引文件
    DefaultMessageStore.this.indexService.deleteExpiredFile(minOffset);
    }
}

```

