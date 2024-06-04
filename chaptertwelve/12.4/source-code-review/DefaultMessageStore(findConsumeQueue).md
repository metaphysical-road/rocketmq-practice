创建MappedFile文件具体代码如下：

```java
//①缓存ConsumeQueue对象的本地缓存
private final ConcurrentMap<String/* topic */, ConcurrentMap<Integer/* 
    queueId */, ConsumeQueue>> consumeQueueTable;
public ConsumeQueue findConsumeQueue(String topic, int queueId) {
    //②用消息主题topic从本地缓存中获取存储ConsumeQueue对象的ConcurrentHashMap
    ConcurrentMap<Integer, ConsumeQueue> map = consumeQueueTable.
       get(topic);
    if (null == map) {
        //③如果为空，则重新创建一个ConcurrentHashMap，初始容量为128
ConcurrentMap<Integer, ConsumeQueue> newMap = new 
ConcurrentHashMap<Integer, ConsumeQueue>(128);
        //④将新的ConcurrentHashMap设置到本地缓存中，key为topic的名称
        ConcurrentMap<Integer, ConsumeQueue> oldMap = 
consumeQueueTable.putIfAbsent(topic, newMap);
        //⑤这里有一个使用ConcurrentHashMap的小技巧，如果oldMap 不为空则设置失败，
            只能使用oldMap，户如果oldMap 为空则设置成功，这样即可使用新的newMap
        if (oldMap != null) {
            map = oldMap;
        } else {
            map = newMap;
        }
    }
    //⑥从map中获取指定消息队列ID的ConsumeQueue 对象
    ConsumeQueue logic = map.get(queueId);
    if (null == logic) {
        //⑦如果logic 为空，则重新初始化一个ConsumeQueue对象
        ConsumeQueue newLogic = new ConsumeQueue(
            topic,queueId,
            StorePathConfigHelper.getStorePathConsumeQueue(this.
messageStoreConfig.getStorePathRootDir()),
            this.getMessageStoreConfig().getMappedFileSizeConsumeQueue()
,this);
        //⑧将新的ConsumeQueue 对象添加到本地缓存中
        ConsumeQueue oldLogic = map.putIfAbsent(queueId, newLogic);
        if (oldLogic != null) {
            logic = oldLogic;
        } else {
            logic = newLogic;
        }
    }
    //⑨返回ConsumeQueue 对象
    return logic;
}                  
```

