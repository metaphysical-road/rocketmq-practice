package com.rocketmq.cloud.youxia.hook;

import com.rocketmq.cloud.youxia.dispatch.ProducerAsyncTraceDispatcher;
import org.apache.rocketmq.client.hook.EndTransactionContext;
import org.apache.rocketmq.client.hook.EndTransactionHook;
import org.apache.rocketmq.client.trace.*;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageType;
import org.apache.rocketmq.common.protocol.NamespaceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CustomEndTransactionHookImpl implements EndTransactionHook {

    @Autowired
    private ProducerAsyncTraceDispatcher producerAsyncTraceDispatcher;

    @Override
    public String hookName() {
        return "CustomEndTransactionHook";
    }

    @Override
    public void endTransaction(EndTransactionContext context) {
        //if it is message trace data,then it doesn't recorded
        if (context == null || context.getMessage().getTopic().startsWith(( producerAsyncTraceDispatcher).getTraceTopicName())) {
            return;
        }
        Message msg = context.getMessage();
        //build the context content of TuxeTraceContext
        TraceContext tuxeContext = new TraceContext();
        tuxeContext.setTraceBeans(new ArrayList<TraceBean>(1));
        tuxeContext.setTraceType(TraceType.EndTransaction);
        tuxeContext.setGroupName(NamespaceUtil.withoutNamespace(context.getProducerGroup()));
        //build the data bean object of message trace
        TraceBean traceBean = new TraceBean();
        traceBean.setTopic(NamespaceUtil.withoutNamespace(context.getMessage().getTopic()));
        traceBean.setTags(context.getMessage().getTags());
        traceBean.setKeys(context.getMessage().getKeys());
        traceBean.setStoreHost(context.getBrokerAddr());
        traceBean.setMsgType(MessageType.Trans_msg_Commit);
        traceBean.setClientHost((producerAsyncTraceDispatcher).getHostProducer().getmQClientFactory().getClientId());
        traceBean.setMsgId(context.getMsgId());
        traceBean.setTransactionState(context.getTransactionState());
        traceBean.setTransactionId(context.getTransactionId());
        traceBean.setFromTransactionCheck(context.isFromTransactionCheck());
        String regionId = msg.getProperty(MessageConst.PROPERTY_MSG_REGION);
        if (regionId == null || regionId.isEmpty()) {
            regionId = MixAll.DEFAULT_TRACE_REGION_ID;
        }
        tuxeContext.setRegionId(regionId);
        tuxeContext.getTraceBeans().add(traceBean);
        tuxeContext.setTimeStamp(System.currentTimeMillis());
        producerAsyncTraceDispatcher.append(tuxeContext);
    }
}
