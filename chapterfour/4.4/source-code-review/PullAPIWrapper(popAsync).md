调用PullAPIWrapper类的popAsync()方法处理消费消息的请求，并构造RPC请求对象PopMessageRequestHeader的具体代码如下：

```java
public void popAsync(MessageQueue mq, long invisibleTime,
                 int maxNums, String consumerGroup,long timeout,
                 PopCallback popCallback, boolean poll, int initMode, 
                 boolean order, String expressionType, String expression)
    throws MQClientException, RemotingException, InterruptedException {
    //①从客户端本地缓存中获取指定Broker名称和Master角色的Broker Server信息
FindBrokerResult findBrokerResult = this.mQClientFactory.
findBrokerAddressInSubscribe(mq.getBrokerName(),
              MixAll.MASTER_ID, true);
    if (null == findBrokerResult) {
        //②如果本地缓存中没有，则从Name Server远程获取Broker Server信息，并更
           新到本地缓存中
        this.mQClientFactory.
updateTopicRouteInfoFromNameServer(mq.getTopic());
          //③重新从本地缓存中获取Broker Server信息
findBrokerResult = this.mQClientFactory.
    findBrokerAddressInSubscribe(mq.getBrokerName(), 
       MixAll.MASTER_ID, true);
    }
    if (findBrokerResult != null) {
        //④构造pop模式消费消息的RPC对象请求头
        PopMessageRequestHeader requestHeader = new 
            PopMessageRequestHeader();
        requestHeader.setConsumerGroup(consumerGroup);
        requestHeader.setTopic(mq.getTopic());
        requestHeader.setQueueId(mq.getQueueId());
        requestHeader.setMaxMsgNums(maxNums);
        requestHeader.setInvisibleTime(invisibleTime);
        requestHeader.setInitMode(initMode);
        requestHeader.setExpType(expressionType);
        requestHeader.setExp(expression);
        requestHeader.setOrder(order);
        //⑤默认开启轮询
        if (poll) {
            requestHeader.setPollTime(timeout);
            requestHeader.setBornTime(System.currentTimeMillis());
           //⑥轮询时间为timeout += 10 * 1000，其中timeout 为15*1000，总计25s
            timeout += 10 * 1000;
        }
        //⑦获取Broker Server的IP地址
        String brokerAddr = findBrokerResult.getBrokerAddr();
        //⑧调用MQClientAPIImpl类的popMessageAsync()方法，拉取消息
        this.mQClientFactory.getMQClientAPIImpl()
.popMessageAsync(mq.getBrokerName(), brokerAddr, 
requestHeader, timeout, popCallback);
        return;
    }
    throw new MQClientException("The broker[" + mq.getBrokerName() 
        + "] not exist", null);
}
```

