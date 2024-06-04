package com.rocketmq.cloud.youxia;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rocketmq.cloud.youxia.util.JsonObjectUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.List;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AlertInfoJsonTest {
    private String testData="{\"receiver\":\"rocketmq-exporter-alarm\",\"status\":\"firing\",\"alerts\":[{\"status\":\"firing\",\"labels\":{\"alertname\":\"consumer delay\",\"broker\":\"broker-b\",\"cluster\":\"DefaultCluster\",\"consumergroup\":\"dingdingMonitorGroup\",\"group\":\"rocketmq-exporter-alarm\",\"severity\":\"warning\",\"topic\":\"dingdingMonitor\"},\"annotations\":{\"description\":\"消息消费延迟：当消息生产后超过12小时还未被消费，持续5分钟，触发此报警\",\"message\":\"消费者消费延迟超过12小时，持续时间超过5分钟，信息如下：\\\\n 集群：DefaultCluster；\\\\n Topic：dingdingMonitor；\\\\n Group：dingdingMonitorGroup；\\\\n 延迟时间：265.37 小时\"},\"startsAt\":\"2022-05-05T10:05:25.875Z\",\"endsAt\":\"0001-01-01T00:00:00Z\",\"generatorURL\":\"http://b7476878bf0b:9090/graph?g0.expr=label_replace%28%28sum+by%28cluster%2C+broker%2C+group%2C+topic%29+%28rocketmq_group_get_latency_by_storetime%29+%2F+%281000+%2A+60+%2A+60%29%29+%3E+12%2C+%22consumergroup%22%2C+%22%241%22%2C+%22group%22%2C+%22%28.%2A%29%22%29&g0.tab=1\",\"fingerprint\":\"efc7e7bb9143ab04\"},{\"status\":\"firing\",\"labels\":{\"alertname\":\"consumer delay\",\"broker\":\"broker-b\",\"cluster\":\"DefaultCluster\",\"consumergroup\":\"skywalkingConsumerMonitorGroup\",\"group\":\"rocketmq-exporter-alarm\",\"severity\":\"warning\",\"topic\":\"skywalkingMonitor\"},\"annotations\":{\"description\":\"消息消费延迟：当消息生产后超过12小时还未被消费，持续5分钟，触发此报警\",\"message\":\"消费者消费延迟超过12小时，持续时间超过5分钟，信息如下：\\\\n 集群：DefaultCluster；\\\\n Topic：skywalkingMonitor；\\\\n Group：skywalkingConsumerMonitorGroup；\\\\n 延迟时间：64.04 小时\"},\"startsAt\":\"2022-05-05T10:05:25.875Z\",\"endsAt\":\"0001-01-01T00:00:00Z\",\"generatorURL\":\"http://b7476878bf0b:9090/graph?g0.expr=label_replace%28%28sum+by%28cluster%2C+broker%2C+group%2C+topic%29+%28rocketmq_group_get_latency_by_storetime%29+%2F+%281000+%2A+60+%2A+60%29%29+%3E+12%2C+%22consumergroup%22%2C+%22%241%22%2C+%22group%22%2C+%22%28.%2A%29%22%29&g0.tab=1\",\"fingerprint\":\"6e210341017dc1fc\"},{\"status\":\"firing\",\"labels\":{\"alertname\":\"consumer delay\",\"broker\":\"broker-b\",\"cluster\":\"DefaultCluster\",\"consumergroup\":\"transactionTraceMessage\",\"group\":\"rocketmq-exporter-alarm\",\"severity\":\"warning\",\"topic\":\"transactionTraceMessage\"},\"annotations\":{\"description\":\"消息消费延迟：当消息生产后超过12小时还未被消费，持续5分钟，触发此报警\",\"message\":\"消费者消费延迟超过12小时，持续时间超过5分钟，信息如下：\\\\n 集群：DefaultCluster；\\\\n Topic：transactionTraceMessage；\\\\n Group：transactionTraceMessage；\\\\n 延迟时间：351.88 小时\"},\"startsAt\":\"2022-05-05T10:05:25.875Z\",\"endsAt\":\"0001-01-01T00:00:00Z\",\"generatorURL\":\"http://b7476878bf0b:9090/graph?g0.expr=label_replace%28%28sum+by%28cluster%2C+broker%2C+group%2C+topic%29+%28rocketmq_group_get_latency_by_storetime%29+%2F+%281000+%2A+60+%2A+60%29%29+%3E+12%2C+%22consumergroup%22%2C+%22%241%22%2C+%22group%22%2C+%22%28.%2A%29%22%29&g0.tab=1\",\"fingerprint\":\"7f6b92b6084b630d\"},{\"status\":\"firing\",\"labels\":{\"alertname\":\"consumer delay\",\"broker\":\"broker-b\",\"cluster\":\"DefaultCluster\",\"consumergroup\":\"transactionTraceMessageGroup\",\"group\":\"rocketmq-exporter-alarm\",\"severity\":\"warning\",\"topic\":\"transactionTraceMessage\"},\"annotations\":{\"description\":\"消息消费延迟：当消息生产后超过12小时还未被消费，持续5分钟，触发此报警\",\"message\":\"消费者消费延迟超过12小时，持续时间超过5分钟，信息如下：\\\\n 集群：DefaultCluster；\\\\n Topic：transactionTraceMessage；\\\\n Group：transactionTraceMessageGroup；\\\\n 延迟时间：351.88 小时\"},\"startsAt\":\"2022-05-05T10:05:25.875Z\",\"endsAt\":\"0001-01-01T00:00:00Z\",\"generatorURL\":\"http://b7476878bf0b:9090/graph?g0.expr=label_replace%28%28sum+by%28cluster%2C+broker%2C+group%2C+topic%29+%28rocketmq_group_get_latency_by_storetime%29+%2F+%281000+%2A+60+%2A+60%29%29+%3E+12%2C+%22consumergroup%22%2C+%22%241%22%2C+%22group%22%2C+%22%28.%2A%29%22%29&g0.tab=1\",\"fingerprint\":\"5174d81f93145194\"}],\"groupLabels\":{},\"commonLabels\":{\"alertname\":\"consumer delay\",\"broker\":\"broker-b\",\"cluster\":\"DefaultCluster\",\"group\":\"rocketmq-exporter-alarm\",\"severity\":\"warning\"},\"commonAnnotations\":{\"description\":\"消息消费延迟：当消息生产后超过12小时还未被消费，持续5分钟，触发此报警\"},\"externalURL\":\"http://daf5fcf1865c:9093\",\"version\":\"4\",\"groupKey\":\"{}:{}\",\"truncatedAlerts\":0}";

    @Test
    public void testJsonParse(){
        Map<String,Object> mapData=(Map<String, Object>) JSON.parse(testData);
        List<Map<String,String>> result=JsonObjectUtil.parse(mapData);
    }

    @Test
    public void testDingdingAlarm(){
        //undo
    }
}
