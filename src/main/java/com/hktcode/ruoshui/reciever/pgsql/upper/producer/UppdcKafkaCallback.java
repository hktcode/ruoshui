/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.method.*;
import com.hktcode.bgsimple.status.SimpleStatusCmd;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.consumer.UpcsmParamsPstRecvLsn;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.postgresql.replication.LogSequenceNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

public class UppdcKafkaCallback implements Callback
{
    public static UppdcKafkaCallback of //
        /* */( LogSequenceNumber lsn //
        /* */, SimpleHolder holder //
        /* */, Producer<byte[], byte[]> producer //
        /* */)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        if (holder == null) {
            throw new ArgumentNullException("holder");
        }
        if (producer == null) {
            throw new ArgumentNullException("producer");
        }
        return new UppdcKafkaCallback(lsn, holder, producer);
    }

    private static final Logger logger //
        = LoggerFactory.getLogger(UppdcKafkaCallback.class);

    private final LogSequenceNumber lsn;

    private final Producer<byte[], byte[]> producer;

    private final SimpleHolder holder;

    private UppdcKafkaCallback //
        /* */( LogSequenceNumber lsn //
        /* */, SimpleHolder holder //
        /* */, Producer<byte[], byte[]> producer //
        /* */)
    {
        this.lsn = lsn;
        this.holder = holder;
        this.producer = producer;
    }

    @Override
    public void onCompletion(RecordMetadata metadata, Exception ex)
    {
        try {
            if (ex != null) {
                // TODO: 此处不应该对每条记录都将this.producer.close()方法执行一遍
                // 这方面看来，Kafka客户端的设计并不是非常合理，缺少批量处理的能力
                logger.error("kafka producer send record fail: lsn={}", this.lsn, ex);
                // 如果已经关闭了，再次调用close不会抛出异常：
                // 但是ex的值固定为：
                // java.lang.IllegalStateException: Producer is closed forcefully.
                //	at org.apache.kafka.clients.producer.internals.RecordAccumulator.abortBatches(RecordAccumulator.java:696) [kafka-clients-1.1.0.jar:na]
                //	at org.apache.kafka.clients.producer.internals.RecordAccumulator.abortIncompleteBatches(RecordAccumulator.java:683) [kafka-clients-1.1.0.jar:na]
                //	at org.apache.kafka.clients.producer.internals.Sender.run(Sender.java:185) [kafka-clients-1.1.0.jar:na]
                //	at java.lang.Thread.run(Thread.java:745) [na:1.8.0_121]

                this.producer.close(0, TimeUnit.MILLISECONDS);
                SimpleMethod[] method = new SimpleMethod[3];
                method[0] = SimpleMethodParamsDelDefault.of(); // TODO:
                method[1] = SimpleMethodParamsDelDefault.of();
                method[2] = SimpleMethodParamsDelDefault.of();
                Phaser phaser = new Phaser(3);
                SimpleStatusCmd del = SimpleStatusCmd.of(phaser, method);
                this.holder.cmd(del);
            }
            else if (this.lsn.asLong() != LogSequenceNumber.INVALID_LSN.asLong()) {
                logger.info("kafka producer send record success: lsn={}", this.lsn);
                SimpleMethod[] method = new SimpleMethod[3];
                method[0] = UpcsmParamsPstRecvLsn.of(this.lsn);
                method[1] = SimpleMethodParamsPstDefault.of();
                method[2] = SimpleMethodParamsPstDefault.of();
                Phaser phaser = new Phaser(3);
                SimpleStatusCmd pst = SimpleStatusCmd.of(phaser, method);
                this.holder.cmd(pst);
            }
        } catch (InterruptedException e) {
            logger.error("should never happen", ex);
            Thread.currentThread().interrupt();
        }
    }
}
