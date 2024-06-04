在RebalanceImpl类的rebalanceByTopic()方法中处理“集群”模式的负载均衡，具体代码如下：

```java
case CLUSTERING: {
    //①获取消息主题的消息队列信息
    Set<MessageQueue> mqSet = this.topicSubscribeInfoTable.get(topic);
    //②获取指定消息主题和消费者组的所有消费者客户端的实例ID
    List<String> cidAll = this.mQClientFactory.findConsumerIdList(topic, 
        consumerGroup);
    if (null == mqSet) {
        if (!topic.startsWith(MixAll.RETRY_GROUP_TOPIC_PREFIX)) {
            log.warn("doRebalance, {}, but the topic[{}] not exist.", 
             consumerGroup, topic);
        }
    }
    if (null == cidAll) {
        log.warn("doRebalance, {} {}, get consumer id list failed", 
            consumerGroup, topic);
    }
    if (mqSet != null && cidAll != null) {
        List<MessageQueue> mqAll = new ArrayList<MessageQueue>();
        mqAll.addAll(mqSet);
        //③排序消费队列列表
        Collections.sort(mqAll);
        //④排序消费者客户端的实例ID列表
        Collections.sort(cidAll);
        //⑤获取负载均衡策略，默认为平均负载均衡
        AllocateMessageQueueStrategy strategy = 
          this.allocateMessageQueueStrategy;
        List<MessageQueue> allocateResult = null;
        try {
            //⑥执行负载均衡策略，得到负载均衡后的消息队列列表
            allocateResult = strategy.allocate(
                this.consumerGroup,this.mQClientFactory.getClientId(),
                        mqAll,cidAll);
        } catch (Throwable e) {
            log.error("AllocateMessageQueueStrategy.allocate Exception. 
                allocateMessageQueueStrategyName={}", strategy.getName(),e);
            return;
        }
        Set<MessageQueue> allocateResultSet = new HashSet<MessageQueue>();
        if (allocateResult != null) {
            allocateResultSet.addAll(allocateResult);
        }
        //⑦更新缓存中消费消息的“处理队列”和“消费队列”之间的映射关系
        boolean changed = this.updateProcessQueueTableInRebalance(topic, 
           allocateResultSet, isOrder);
        if (changed) {
            log.info(
            "rebalanced result changed. allocateMessageQueueStrategyName={}, 
                group={}, topic={}, clientId={}, mqAllSize={}, cidAllSize={}, 
                 rebalanceResultSize={}, rebalanceResultSet={}",
                    strategy.getName(), consumerGroup, topic, 
                     this.mQClientFactory.getClientId(), mqSet.size(), 
                     cidAll.size(),allocateResultSet.size(), 
                       allocateResultSet);
            //⑧如果订阅关系已经更新，则通知消费消息的监听器（pull模式消费消息的入口）
            this.messageQueueChanged(topic, mqSet, allocateResultSet);
        }
    }
    break;
}
```

