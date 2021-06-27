package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.kafka.Kafka;
import com.hktcode.lang.exception.ArgumentIllegalException;
import com.hktcode.lang.exception.ArgumentNegativeException;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlValTxactCommit;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.hktcode.kafka.Kafka.Serializers.BYTES;
import static com.hktcode.ruoshui.Ruoshui.THE_NAME;

public class UppdcSenderKafka extends UppdcSender
{
    private static final Logger logger = LoggerFactory.getLogger(UppdcSenderKafka.class);

    public static final String TARGET_TOPIC = THE_NAME;

    public static final int PARTITION_NO = 0;

    public static UppdcSenderKafka of(JsonNode json, AtomicLong xidlsn)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        if (xidlsn == null) {
            throw new ArgumentNullException("xidlsn");
        }
        Map<String, String> kfkMap = createDefaultMap();
        JsonNode kfkNode = json.get("kfk_property");
        if (kfkNode != null) {
            JacksonObject.merge(kfkMap, kfkNode);
        }
        ImmutableMap<String, String> kfkProperty = ImmutableMap.copyOf(kfkMap);
        // TODO: 检查properties

        UppdcSenderKafka result =  new UppdcSenderKafka(kfkProperty, xidlsn);

        String targetTopic = json.path("target_topic").asText(result.targetTopic);
        if (!Kafka.TOPIC_PATTERN.matcher(targetTopic).matches()) {
            throw new ArgumentIllegalException("topic name is not match the pattern", "targetTopic", targetTopic); // TODO:
        }
        result.targetTopic = targetTopic;

        int partitionNo = json.path("partition_no").asInt(result.partitionNo);
        if (partitionNo < 0) {
            // TODO:
            throw new ArgumentNegativeException("partitionNo", partitionNo);
        }
        result.partitionNo = partitionNo;
        return result;
    }

    @Override
    public Client client()
    {
        return new Client(this);
    }

    private static Map<String, String> createDefaultMap()
    {
        Map<String, String> result = new HashMap<>();
        result.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        return result;
    }

    private UppdcSenderKafka(ImmutableMap<String, String> kfkProperty, AtomicLong xidlsn)
    {
        super(xidlsn);
        this.kfkProperty = kfkProperty;
    }

    // handle
    private final List<Producer<byte[], byte[]>> innerHandle = new ArrayList<>(1);

    // argval
    public final ImmutableMap<String, String> kfkProperty;

    public String targetTopic = TARGET_TOPIC;

    public int partitionNo = PARTITION_NO;

    public class Client implements UppdcSender.Client
    {
        private final UppdcSenderKafka squeue;

        private Client(UppdcSenderKafka squeue)
        {
            this.squeue = squeue;
            Properties properties = new Properties();
            properties.setProperty("request.timeout.ms", "1000");
            for (Map.Entry<String, String> e : kfkProperty.entrySet()) {
                properties.setProperty(e.getKey(), e.getValue());
            }
            this.squeue.innerHandle.add(new KafkaProducer<>(properties, BYTES, BYTES));
        }

        @Override
        public void send(UpperRecordProducer record) throws Throwable
        {
            Throwable ex = this.squeue.callbackRef.get();
            if (ex != null) {
                throw ex;
            }
            String keyText = record.key.toJsonObject().toString();
            String valText = record.val.toJsonObject().toString();
            String t = this.squeue.targetTopic;
            int p = this.squeue.partitionNo;
            byte[] k = keyText.getBytes(StandardCharsets.UTF_8);
            byte[] v = valText.getBytes(StandardCharsets.UTF_8);
            ProducerRecord<byte[], byte[]> r = new ProducerRecord<>(t, p, k, v);
            // TODO: kafka生产者的行为好奇怪
            Producer<byte[], byte[]> d = this.squeue.innerHandle.get(0);
            d.send(r, (m, e)->this.onCompletion(record, e));
        }

        @Override
        public void close()
        {
            for (Producer<byte[], byte[]> handle: this.squeue.innerHandle) {
                handle.close(0, TimeUnit.MILLISECONDS);
            }
            this.squeue.innerHandle.clear();
        }

        private void onCompletion(UpperRecordProducer record, Exception ex)
        {
            if (ex != null) {
                logger.error("kafka producer send record fail: lsn={}", record, ex);
                if (this.squeue.callbackRef.compareAndSet(null, ex)) {
                    // kafka客户端的行为好奇怪，不符合一般的Java类调用约定：
                    // 1. 通常Java类中，应该是谁创建谁关闭。
                    //    Kafka的Producer虽然也满足这个条件，但是如果此处ex不是null，必须在此方法中调用close。
                    // 2. Kafka后端是批量发送的，所以一旦发送失败，其实是多条发送失败，此方法会被调用多次。
                    //    如果简单的执行close，则会关闭producer.close()多次，虽然producer.close确实可以多次调用。
                    //    但每次执行close感觉不是那么优雅。
                    //    我采用了设置callbackRef成功才关闭producer，这样子就只会调用producer.close()一次。
                    //    如果已经关闭了，再次调用close不会抛出异常：
                    //    但是ex的值固定为：
                    //    java.lang.IllegalStateException: Producer is closed forcefully.
                    //	     at org.apache.kafka.clients.producer.internals.RecordAccumulator.abortBatches(RecordAccumulator.java:696) [kafka-clients-1.1.0.jar:na]
                    //	     at org.apache.kafka.clients.producer.internals.RecordAccumulator.abortIncompleteBatches(RecordAccumulator.java:683) [kafka-clients-1.1.0.jar:na]
                    //	     at org.apache.kafka.clients.producer.internals.Sender.run(Sender.java:185) [kafka-clients-1.1.0.jar:na]
                    //	     at java.lang.Thread.run(Thread.java:745) [na:1.8.0_121]
                    // 从这两方面来看，Kafka客户端的设计并不合理
                    this.close();
                }
            }
            else if (record.val instanceof PgsqlValTxactCommit) {
                long lsn = ((PgsqlValTxactCommit)record.val).lsnofmsg;
                logger.info("kafka producer send record success: lsn={}", lsn);
                this.squeue.txactionLsn.set(lsn);
            }
        }
    }
}
