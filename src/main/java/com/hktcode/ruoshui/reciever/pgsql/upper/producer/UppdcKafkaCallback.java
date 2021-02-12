/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.postgresql.replication.LogSequenceNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class UppdcKafkaCallback implements Callback
{
    private static final Logger logger = LoggerFactory.getLogger(UppdcKafkaCallback.class);

    public static UppdcKafkaCallback of //
        /* */( LogSequenceNumber lsn //
        /* */, UppdcMetric metric //
        /* */, Producer<byte[], byte[]> producer //
        /* */)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (producer == null) {
            throw new ArgumentNullException("producer");
        }
        return new UppdcKafkaCallback(lsn, metric, producer);
    }

    private final LogSequenceNumber lsn;

    private final Producer<byte[], byte[]> producer;

    private final UppdcMetric metric;

    private UppdcKafkaCallback //
        /* */( LogSequenceNumber lsn //
        /* */, UppdcMetric metric //
        /* */, Producer<byte[], byte[]> producer //
        /* */)
    {
        this.lsn = lsn;
        this.metric = metric;
        this.producer = producer;
    }

    @Override
    public void onCompletion(RecordMetadata metadata, Exception ex)
    {
        if (ex != null) {
            logger.error("kafka producer send record fail: lsn={}", this.lsn, ex);
            if (this.metric.callbackRef.compareAndSet(null, ex)) {
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
                // 从这两方面来看，Kafka客户端的设计并不是非常合理
                this.producer.close(0, TimeUnit.MILLISECONDS);
            }
        }
        else if (this.lsn.asLong() != LogSequenceNumber.INVALID_LSN.asLong()) {
            logger.info("kafka producer send record success: lsn={}", this.lsn);
            this.metric.txactionLsn.set(this.lsn.asLong());
        }
    }
}
