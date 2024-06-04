用IndexService类的deleteExpiredFile()方法清理过期的索引文件的具体代码如下：

```java
public void deleteExpiredFile(long offset) {
    Object[] files = null;
    try {
        //①获取读锁
        this.readWriteLock.readLock().lock();
        if (this.indexFileList.isEmpty()) {
            return;
        }
        //②获取索引文件列表中第1个索引文件的最大的物理消费位置Offset
        long endPhyOffset = this.indexFileList.get(0).getEndPhyOffset();
        if (endPhyOffset < offset) {
       //③如果最大的物理消费位置小于当前需要删除的消费位置，则遍历整个索引文件列表
            files = this.indexFileList.toArray();
        }
    } catch (Exception e) {
        log.error("destroy exception", e);
    } finally {
        this.readWriteLock.readLock().unlock();
    }
    if (files != null) {
        //④遍历索引文件列表
        List<IndexFile> fileList = new ArrayList<IndexFile>();
        for (int i = 0; i < (files.length - 1); i++) {
            IndexFile f = (IndexFile) files[i];
//⑤如果索引文件的最大消费位置小于当前需要删除的消费位置，则将它添加到需要删除
    的索引文件列表中
            if (f.getEndPhyOffset() < offset) {
                fileList.add(f);
            } else {
                break;
            }
        }
        //⑥执行deleteExpiredFile()方法删除过滤出来的需要删除的索引文件列表
        this.deleteExpiredFile(fileList);
    }
}
```

