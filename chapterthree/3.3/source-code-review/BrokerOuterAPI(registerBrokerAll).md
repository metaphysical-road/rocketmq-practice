调用BrokerOuterAPI类的registerBrokerAll()方法，去注册Broker Server的消息路由信息，具体代码如下（为了方便阅读，这里省略了registerBrokerAll()方法的部分入参。如果需要了解细节，可以查阅相关源码。）

```java
public List<RegisterBrokerResult> registerBrokerAll(
    final String clusterName,final String brokerAddr,…) {
    final List<RegisterBrokerResult> registerBrokerResultList = new 
        CopyOnWriteArrayList<>();
    //①从Name Server中，获取Name Server节点的IP地址列表
    List<String> nameServerAddressList = 
        this.remotingClient.getNameServerAddressList();
    if (nameServerAddressList != null && nameServerAddressList.
        size() > 0) {
        //②构造注册Broker Server信息的RPC请求头对象
       final RegisterBrokerRequestHeader requestHeader = new 
            RegisterBrokerRequestHeader();
        //③设置“注册Broker Server”命令事件的请求头信息
        requestHeader.setBrokerAddr(brokerAddr);
        requestHeader.setBrokerId(brokerId);
        requestHeader.setBrokerName(brokerName);
        requestHeader.setClusterName(clusterName);
        requestHeader.setHaServerAddr(haServerAddr);
        requestHeader.setCompressed(compressed);
        //④构造注册Broker Server信息的RPC请求消息体
        RegisterBrokerBody requestBody = new RegisterBrokerBody();
        //⑤设置完成序列化之后的消息路由信息 
        requestBody.setTopicConfigSerializeWrapper(topicConfigWrapper);
        //⑥如果有过滤服务，则设置过滤服务的IP地址到消息体中
        requestBody.setFilterServerList(filterServerList);
        //⑦压缩消息体
        final byte[] body = requestBody.encode(compressed);
        final int bodyCrc32 = UtilAll.crc32(body);
        //⑧计算冗余校验码，并将其设置到消息头中
        requestHeader.setBodyCrc32(bodyCrc32);
        //⑨用并发控制器CountDownLatch来确保线程安全
        final CountDownLatch countDownLatch = new 
            CountDownLatch(nameServerAddressList.size());
        for (final String namesrvAddr : nameServerAddressList) {
            brokerOuterExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                  //⑩发起RPC请求，注册Broker Server
                        RegisterBrokerResult result =    
                            registerBroker(namesrvAddr, oneway, 
                                    timeoutMills, requestHeader, body);
                        if (result != null) {
                            registerBrokerResultList.add(result);
                        }
                        log.info("register broker[{}]to name server {} OK", 
                            brokerId, namesrvAddr);
                    } catch (Exception e) {
                        log.warn("registerBroker Exception, {}",     
                            namesrvAddr, e);
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            });
        }
        try {
            countDownLatch.await(timeoutMills, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
        }
    }
    return registerBrokerResultList;
}
```

