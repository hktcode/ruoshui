package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlValTxactCommit;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import org.apache.kafka.clients.producer.*;
import org.postgresql.replication.LogSequenceNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.hktcode.kafka.Kafka.Serializers.BYTES;

public class UppdcSenderKafka implements UppdcSender
{
    public static UppdcSenderKafka of(UppdcWkstepArgvalKafka argval, UppdcWkstepGaugesKafka gauges)
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        if (gauges == null) {
            throw new ArgumentNullException("gauges");
        }
        return new UppdcSenderKafka(argval, gauges);
    }

    private final UppdcWkstepArgvalKafka argval;

    private final UppdcWkstepGaugesKafka gauges;

    private final Producer<byte[], byte[]> handle;

    private UppdcSenderKafka(UppdcWkstepArgvalKafka argval, UppdcWkstepGaugesKafka gauges)
    {
        Properties properties = new Properties();
        properties.setProperty("request.timeout.ms", "1000");
        for (Map.Entry<String, String> e : argval.kfkProperty.entrySet()) {
            properties.setProperty(e.getKey(), e.getValue());
        }
        this.argval = argval;
        this.gauges = gauges;
        this.handle = new KafkaProducer<>(properties, BYTES, BYTES);
    }

    @Override
    public void send(UppdcWorkerGauges gauges, UpperRecordProducer record)
    {
        String keyText = record.key.toJsonObject().toString();
        String valText = record.val.toJsonObject().toString();
        String t = argval.targetTopic;
        int p = argval.partitionNo;
        byte[] k = keyText.getBytes(StandardCharsets.UTF_8);
        byte[] v = valText.getBytes(StandardCharsets.UTF_8);
        ProducerRecord<byte[], byte[]> r = new ProducerRecord<>(t, p, k, v);
        LogSequenceNumber lsn = LogSequenceNumber.INVALID_LSN;
        if (record.val instanceof PgsqlValTxactCommit) {
            PgsqlValTxactCommit val = (PgsqlValTxactCommit)record.val;
            lsn = LogSequenceNumber.valueOf(val.lsnofmsg);
        }
        // TODO: kafka生产者的行为好奇怪
        this.handle.send(r, new Handler(lsn, gauges, this.handle));
    }

    @Override
    public void close()
    {
        this.handle.close();
    }

    private static class Handler implements Callback
    {
        private static final Logger logger = LoggerFactory.getLogger(Handler.class);

        private final LogSequenceNumber lsn;

        private final Producer<byte[], byte[]> producer;

        private final UppdcWorkerGauges gauges;

        public Handler //
            /* */(LogSequenceNumber lsn //
            /* */, UppdcWorkerGauges gauges //
            /* */, Producer<byte[], byte[]> producer //
            /* */)
        {
            this.lsn = lsn;
            this.gauges = gauges;
            this.producer = producer;
        }

        @Override
        public void onCompletion(RecordMetadata metadata, Exception ex)
        {
            if (ex != null) {
                logger.error("kafka producer send record fail: lsn={}", this.lsn, ex);
                if (this.gauges.callbackRef.compareAndSet(null, ex)) {
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
                    this.producer.close(0, TimeUnit.MILLISECONDS);
                }
            }
            else if (this.lsn.asLong() != LogSequenceNumber.INVALID_LSN.asLong()) {
                logger.info("kafka producer send record success: lsn={}", this.lsn);
                this.gauges.txactionLsn.set(this.lsn.asLong());
            }
        }
    }
}
