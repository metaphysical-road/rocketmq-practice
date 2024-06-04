用DefaultRequestProcesssor类的processRequest()方法分发路由信息的事件的具体代码如下：

```java
public class DefaultRequestProcessor extends AsyncNettyRequestProcessor 
    implements NettyRequestProcessor {
    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx,
        RemotingCommand request) throws RemotingCommandException {
        switch (request.getCode()) {
            ...
            //①分发注册Broker Server的事件
            case RequestCode.REGISTER_BROKER:
                Version brokerVersion = 
                    MQVersion.value2Version(request.getVersion());
                if (brokerVersion.ordinal() >= 
                    MQVersion.Version.V3_0_11.ordinal()) {
                    return this.registerBrokerWithFilterServer(ctx, request);
                } else {
                    return this.registerBroker(ctx, request);
                }
            //②分发取消注册Broker Server的事件
            case RequestCode.UNREGISTER_BROKER:
                return this.unregisterBroker(ctx, request);
            //③分发获取指定主题的消息路由信息的事件
            case RequestCode.GET_ROUTEINFO_BY_TOPIC:
                return this.getRouteInfoByTopic(ctx, request);
            //④分发获取Broker Server的集群信息的事件
            case RequestCode.GET_BROKER_CLUSTER_INFO:
                return this.getBrokerClusterInfo(ctx, request);
            //⑤分发删除指定Broker Server名称的所有队列的写消息权限的事件
            case RequestCode.WIPE_WRITE_PERM_OF_BROKER:
                return this.wipeWritePermOfBroker(ctx, request);
            //⑥分发新增指定Broker Server名称的所有队列的写消息权限的事件
            case RequestCode.ADD_WRITE_PERM_OF_BROKER:
                return this.addWritePermOfBroker(ctx, request);
            //⑦分发获取所有的消息路由信息的事件
case RequestCode.GET_ALL_TOPIC_LIST_FROM_NAMESERVER:
                return getAllTopicListFromNameserver(ctx, request);
            //⑧分发删除指定主题的消息路由信息的事件
            case RequestCode.DELETE_TOPIC_IN_NAMESRV:
                return deleteTopicInNamesrv(ctx, request);
            //⑨分发获取指定命名空间的键值对配置信息的事件
            case RequestCode.GET_KVLIST_BY_NAMESPACE:
                return this.getKVListByNamespace(ctx, request);
            //⑩分发获取指定集群的消息路由信息的事件
            case RequestCode.GET_TOPICS_BY_CLUSTER:
                return this.getTopicsByCluster(ctx, request);
            ...
            default:
                break;
        }
        return null;
    }
    ...
}
```

