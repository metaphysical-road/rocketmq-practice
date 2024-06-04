Broker Server处理生产消息的请求时，会预校验消息。如果消息中的消息主题对应的消息队列信息不存在，则会创建消息路由信息，并将消息路由信息存储在Broker Server中，然后重新注册Broker Server节点。校验消息的具体代码如下：

```java
//①AbstractSendMessageProcessor类的msgCheck()方法的部分代码
protected RemotingCommand msgCheck(final ChannelHandlerContext ctx,
        final SendMessageRequestHeader requestHeader, final 
     RemotingCommand response) {
    ...
    //②从Broker Server中获取指定消息主题名称的消息路由信息
    TopicConfig topicConfig=this.brokerController.
           getTopicConfigManager().selectTopicConfig(
               requestHeader.getTopic());
    if (null == topicConfig) {
        ...
        //③如果从Broker Server中没有获取消息路由信息，则调TopicConfigManager
           类的createTopicInSendMessageMethod()方法创建消息路由信息
        topicConfig = this.brokerController.getTopicConfigManager().
            createTopicInSendMessageMethod(
                requestHeader.getTopic(),
                requestHeader.getDefaultTopic(),
                RemotingHelper.parseChannelRemoteAddr(ctx.channel()),
                requestHeader.getDefaultTopicQueueNums(), topicSysFlag);
        if (null == topicConfig) {
            //④如果创建失败并且当前消息是重试类型的消息，则重新调用
                    TopicConfigManager类的createTopicInSendMessageMethod()方
                     法创建消息路由信息
            if (requestHeader.getTopic().startsWith(
                MixAll.RETRY_GROUP_TOPIC_PREFIX)) {
                topicConfig =this.brokerController.
                     getTopicConfigManager().
                         createTopicInSendMessageBackMethod(
                             requestHeader.getTopic(), 1, 
                                 PermName.PERM_WRITE |
                                     PermName.PERM_READ,topicSysFlag);
            }
        }
        //⑤如果topicConfig还是为空，则直接返回对应主题的消息路由信息不存在的错误
            提示
        if (null == topicConfig) {
            response.setCode(ResponseCode.TOPIC_NOT_EXIST);
            response.setRemark("topic[" + requestHeader.getTopic() + "] not 
                exist, apply first please!"
                    + FAQUrl.suggestTodo(FAQUrl.APPLY_TOPIC_URL));
            return response;
        }
    }
}

```

