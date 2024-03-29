package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.hktcode.jackson.JacksonObject;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlValTxactCommit;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.hktcode.kafka.Kafka.Serializers.BYTES;
import static com.hktcode.ruoshui.Ruoshui.THE_NAME;

public class SndQueueKafka extends SndQueue
{
    public static final class Schema
    {
        public static final ObjectNode SCHEMA;

        static {
            String filename = "SndQueueKafka.yml";
            SCHEMA=JacksonObject.getFromResource(SndQueue.class, filename);
        }
    }

    public static final String TARGET_TOPIC = THE_NAME;

    public static final int PARTITION_NO = 0;

    public static SndQueueKafka of(JsonNode json, AtomicLong xidlsn)
    {
        if (json == null) {
            throw new ArgumentNullException("json");
        }
        if (xidlsn == null) {
            throw new ArgumentNullException("xidlsn");
        }
        Map<String, String> kfkMap = new HashMap<>();
        kfkMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        JsonNode kfkNode = json.get("kfk_property");
        if (kfkNode != null) {
            JacksonObject.merge(kfkMap, kfkNode);
        }
        ImmutableMap<String, String> kfkProperty = ImmutableMap.copyOf(kfkMap);
        // 未来计划: 检查properties
        SndQueueKafka result =  new SndQueueKafka(kfkProperty, xidlsn);
        result.pst(json);
        return result;
    }

    @Override
    public Client client()
    {
        return new Client(this);
    }

    @Override
    public Result toJsonResult()
    {
        return new Result(new Config(this), new Metric(this));
    }

    private SndQueueKafka //
            (ImmutableMap<String, String> kfkProperty, AtomicLong xidlsn)
    {
        super(xidlsn);
        this.kfkProperty = kfkProperty;
        this.innerHandle = new ArrayList<>(1);
    }

    // handle
    private final List<Producer<byte[], byte[]>> innerHandle;

    // argval
    private final ImmutableMap<String, String> kfkProperty;

    private String targetTopic = TARGET_TOPIC;

    private int partitionNo = PARTITION_NO;

    @Override
    public void pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        this.targetTopic = node.path("target_topic").asText(this.targetTopic);
        this.partitionNo = node.path("partition_no").asInt(this.partitionNo);
        // - 未来计划：检查相关信息
        // - String targetTopic = json.path("target_topic").asText(result.targetTopic);
        // - if (!Kafka.TOPIC_PATTERN.matcher(targetTopic).matches()) {
        // -     throw new ArgumentIllegalException("topic name is not match the pattern", "targetTopic", targetTopic);
        // - }
        // - result.targetTopic = targetTopic;

        // - int partitionNo = json.path("partition_no").asInt(result.partitionNo);
        // - if (partitionNo < 0) {
        // -     throw new ArgumentNegativeException("partitionNo", partitionNo);
        // - }
        // - result.partitionNo = partitionNo;
    }

    public static class Client implements SndQueue.Client
    {
        private final SndQueueKafka squeue;

        private Client(SndQueueKafka squeue)
        {
            Properties props = new Properties();
            props.setProperty("request.timeout.ms", "1000");
            for (Map.Entry<String, String> e : squeue.kfkProperty.entrySet()) {
                props.setProperty(e.getKey(), e.getValue());
            }
            squeue.innerHandle.add(new KafkaProducer<>(props, BYTES, BYTES));
            this.squeue = squeue;
        }

        @Override
        public void send(RhsQueue.Record record) throws Throwable
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

        private void onCompletion(RhsQueue.Record record, Exception ex)
        {
            // kafka生产者的行为好奇怪，不符合的Java的资源管理调用约定：
            // 1. 通常Java类中，应该是谁创建谁关闭。
            //    Kafka的Producer虽然也满足这个条件。
            //    但是如果此处ex不是null，必须在此方法中调用close。
            // 2. Kafka后端批量发送记录。
            //    如果发送失败，其实是多条发送失败。
            //    此时如果简单的执行close，则会调用close多次。
            //    显得不是那么优雅。
            //    通过设置callbackRef成功为条件调用close，保证仅一次调用。
            //    如果已经关闭了，再次调用close不会抛出异常：
            //    但是ex的值固定为：
            //    java.lang.IllegalStateException: Producer is closed forcefully.
            //	     at org.apache.kafka.clients.producer.internals.RecordAccumulator.abortBatches(RecordAccumulator.java:696) [kafka-clients-1.1.0.jar:na]
            //	     at org.apache.kafka.clients.producer.internals.RecordAccumulator.abortIncompleteBatches(RecordAccumulator.java:683) [kafka-clients-1.1.0.jar:na]
            //	     at org.apache.kafka.clients.producer.internals.Sender.run(Sender.java:185) [kafka-clients-1.1.0.jar:na]
            //	     at java.lang.Thread.run(Thread.java:745) [na:1.8.0_121]
            // 从这两方面来看，Kafka客户端的设计并不合理
            if (ex != null) {
                logger.error("kafka producer send fail: record={}", record, ex);
                if (this.squeue.callbackRef.compareAndSet(null, ex)) {
                    this.close();
                }
            }
            else if (record.val instanceof PgsqlValTxactCommit) {
                long lsn = ((PgsqlValTxactCommit)record.val).lsnofmsg;
                logger.info("kafka producer send record success: lsn={}", lsn);
                this.squeue.lastConfirm.set(lsn);
            }
        }
    }

    public static class Config extends SndQueue.Config
    {
        public final ImmutableMap<String, String> kfkProperty;

        public final String targetTopic;

        public final int partitionNo;

        private Config(SndQueueKafka sender)
        {
            super("kafka");
            this.kfkProperty = sender.kfkProperty;
            this.targetTopic = sender.targetTopic;
            this.partitionNo = sender.partitionNo;
        }

        @Override
        public ObjectNode toJsonObject(ObjectNode node)
        {
            if (node == null) {
                throw new ArgumentNullException("node");
            }
            node = super.toJsonObject(node);
            ObjectNode kfkPropertyNode = node.putObject("kfk_property");
            for (Map.Entry<String, String> e : this.kfkProperty.entrySet()) {
                kfkPropertyNode.put(e.getKey(), e.getValue());
            }
            node.put("target_topic", this.targetTopic);
            node.put("partition_no", this.partitionNo);
            return node;
        }
    }

    public static class Metric extends SndQueue.Metric
    {
        private Metric(SndQueue sender)
        {
            super(sender);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(SndQueueKafka.class);
}
