package com.rocketmq.cloud.youxia.hook;

import com.rocketmq.cloud.youxia.dispatch.ConsumerAsyncTraceDispatcher;
import org.apache.rocketmq.client.consumer.listener.ConsumeReturnType;
import org.apache.rocketmq.client.hook.ConsumeMessageContext;
import org.apache.rocketmq.client.hook.ConsumeMessageHook;
import org.apache.rocketmq.client.trace.TraceBean;
import org.apache.rocketmq.client.trace.TraceContext;
import org.apache.rocketmq.client.trace.TraceType;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.NamespaceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CustomTraceConsumerHookImpl implements ConsumeMessageHook {

    @Autowired
    private ConsumerAsyncTraceDispatcher consumerAsyncTraceDispatcher;

    @Override
    public String hookName() {
        return " CustomTraceConsumerHook";
    }

    @Override
    public void consumeMessageBefore(ConsumeMessageContext context) {
        if (context == null || context.getMsgList() == null || context.getMsgList().isEmpty()) {
            return;
        }
        TraceContext traceContext = new TraceContext();
        context.setMqTraceContext(traceContext);
        traceContext.setTraceType(TraceType.SubBefore);//
        traceContext.setGroupName(NamespaceUtil.withoutNamespace(context.getConsumerGroup()));//
        List<TraceBean> beans = new ArrayList<TraceBean>();
        for (MessageExt msg : context.getMsgList()) {
            if (msg == null) {
                continue;
            }
            String regionId = msg.getProperty(MessageConst.PROPERTY_MSG_REGION);
            String traceOn = msg.getProperty(MessageConst.PROPERTY_TRACE_SWITCH);

            if (traceOn != null && traceOn.equals("false")) {
                // If trace switch is false ,skip it
                continue;
            }
            TraceBean traceBean = new TraceBean();
            traceBean.setTopic(NamespaceUtil.withoutNamespace(msg.getTopic()));//
            traceBean.setMsgId(msg.getMsgId());//
            traceBean.setTags(msg.getTags());//
            traceBean.setKeys(msg.getKeys());//
            traceBean.setStoreTime(msg.getStoreTimestamp());//
            traceBean.setBodyLength(msg.getStoreSize());//
            traceBean.setRetryTimes(msg.getReconsumeTimes());//
            traceContext.setRegionId(regionId);//
            beans.add(traceBean);
        }
        if (beans.size() > 0) {
            traceContext.setTraceBeans(beans);
            traceContext.setTimeStamp(System.currentTimeMillis());
            consumerAsyncTraceDispatcher.append(traceContext);
        }
    }

    @Override
    public void consumeMessageAfter(ConsumeMessageContext context) {
        if (context == null || context.getMsgList() == null || context.getMsgList().isEmpty()) {
            return;
        }
        TraceContext subBeforeContext = (TraceContext) context.getMqTraceContext();

        if (subBeforeContext.getTraceBeans() == null || subBeforeContext.getTraceBeans().size() < 1) {
            // If subBefore bean is null ,skip it
            return;
        }
        TraceContext subAfterContext = new TraceContext();
        subAfterContext.setTraceType(TraceType.SubAfter);//
        subAfterContext.setRegionId(subBeforeContext.getRegionId());//
        subAfterContext.setGroupName(NamespaceUtil.withoutNamespace(subBeforeContext.getGroupName()));//
        subAfterContext.setRequestId(subBeforeContext.getRequestId());//
        subAfterContext.setSuccess(context.isSuccess());//

        // Calculate the cost time for processing messages
        int costTime = (int) ((System.currentTimeMillis() - subBeforeContext.getTimeStamp()) / context.getMsgList().size());
        subAfterContext.setCostTime(costTime);//
        subAfterContext.setTraceBeans(subBeforeContext.getTraceBeans());
        Map<String, String> props = context.getProps();
        if (props != null) {
            String contextType = props.get(MixAll.CONSUME_CONTEXT_TYPE);
            if (contextType != null) {
                subAfterContext.setContextCode(ConsumeReturnType.valueOf(contextType).ordinal());
            }
        }
        consumerAsyncTraceDispatcher.append(subAfterContext);
    }
}
