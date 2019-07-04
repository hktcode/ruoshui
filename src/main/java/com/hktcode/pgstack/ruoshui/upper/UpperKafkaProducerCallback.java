/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper;

import com.hktcode.bgmethod.SimpleDelDefaultBgParams;
import com.hktcode.bgmethod.SimpleDelFailureBgParams;
import com.hktcode.bgmethod.SimplePstDefaultBgParams;
import com.hktcode.bgtriple.TripleSwitcher;
import com.hktcode.bgtriple.status.TripleDelBgStatus;
import com.hktcode.bgtriple.status.TriplePstBgStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.postgresql.replication.LogSequenceNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

public class UpperKafkaProducerCallback implements Callback
{
    public static UpperKafkaProducerCallback of //
        /* */( LogSequenceNumber lsn //
        /* */, TripleSwitcher<UpperConsumer, UpperJunction, UpperProducer> switcher //
        /* */, Producer<byte[], byte[]> producer //
        /* */)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        if (switcher == null) {
            throw new ArgumentNullException("switcher");
        }
        if (producer == null) {
            throw new ArgumentNullException("producer");
        }
        return new UpperKafkaProducerCallback(lsn, switcher, producer);
    }

    private static final Logger logger //
        = LoggerFactory.getLogger(UpperKafkaProducerCallback.class);

    private final LogSequenceNumber lsn;

    private final Producer<byte[], byte[]> producer;

    private final TripleSwitcher<UpperConsumer, UpperJunction, UpperProducer> switcher;

    private UpperKafkaProducerCallback //
        /* */( LogSequenceNumber lsn //
        /* */, TripleSwitcher<UpperConsumer, UpperJunction, UpperProducer> switcher //
        /* */, Producer<byte[], byte[]> producer //
        /* */)
    {
        this.lsn = lsn;
        this.switcher = switcher;
        this.producer = producer;
    }

    @Override
    public void onCompletion(RecordMetadata metadata, Exception ex)
    {
        if (ex != null) {
            // TODO: 此处不应该对每条记录都将this.producer.close()方法执行一遍
            logger.error("kafka producer send record fail: lsn={}", this.lsn, ex);
            // 如果已经关闭了，再次调用close不会抛出异常：
            // 但是ex的值固定为：
            // java.lang.IllegalStateException: Producer is closed forcefully.
            //	at org.apache.kafka.clients.producer.internals.RecordAccumulator.abortBatches(RecordAccumulator.java:696) [kafka-clients-1.1.0.jar:na]
            //	at org.apache.kafka.clients.producer.internals.RecordAccumulator.abortIncompleteBatches(RecordAccumulator.java:683) [kafka-clients-1.1.0.jar:na]
            //	at org.apache.kafka.clients.producer.internals.Sender.run(Sender.java:185) [kafka-clients-1.1.0.jar:na]
            //	at java.lang.Thread.run(Thread.java:745) [na:1.8.0_121]

            this.producer.close(0, TimeUnit.MILLISECONDS);
            ZonedDateTime endtime = ZonedDateTime.now();
            SimpleDelDefaultBgParams<UpperConsumer> s = SimpleDelDefaultBgParams.of();
            SimpleDelDefaultBgParams<UpperJunction> p = SimpleDelDefaultBgParams.of();
            SimpleDelFailureBgParams<UpperProducer> d = SimpleDelFailureBgParams.of(ex, endtime);
            TripleDelBgStatus<UpperConsumer, UpperJunction, UpperProducer> status = TripleDelBgStatus.of(s, p, d);
            this.switcher.del(status);
        }
        else if (this.lsn.asLong() != LogSequenceNumber.INVALID_LSN.asLong()) {
            logger.info("kafka producer send record success: lsn={}", this.lsn);
            UpperLastReceiveLsnPstParams s = UpperLastReceiveLsnPstParams.of(this.lsn);
            SimplePstDefaultBgParams<UpperJunction> p = SimplePstDefaultBgParams.of();
            SimplePstDefaultBgParams<UpperProducer> d = SimplePstDefaultBgParams.of();
            TriplePstBgStatus<UpperConsumer, UpperJunction, UpperProducer> status = TriplePstBgStatus.of(s, p, d);
            this.switcher.pst(status);
        }
    }
}
