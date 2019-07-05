/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper;

import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.bgsimple.triple.TripleProducerMetric;
import com.hktcode.bgsimple.triple.kafka.KafkaTripleProducer;
import com.hktcode.lang.RunnableWithInterrupted;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgsqlValTxactCommit;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperProducerConfig;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperProducerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.postgresql.replication.LogSequenceNumber;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import static com.hktcode.kafka.Kafka.Serializers.BYTES;

public class UpperProducer extends KafkaTripleProducer
    /* */< UpperProducer
    /* */, UpperProducerConfig
    /* */, TripleProducerMetric
    /* */, UpperProducerRecord
    /* */, byte[]
    /* */, byte[]
    /* */> //
    implements RunnableWithInterrupted
{
    public static UpperProducer of//
        /* */( UpperProducerConfig config
        /* */, AtomicReference<SimpleStatus> status
        /* */, BlockingQueue<UpperProducerRecord> getout
        /* */)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (getout == null) {
            throw new ArgumentNullException("getout");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new UpperProducer(config, getout, status);
    }

    private UpperProducer //
        /* */( UpperProducerConfig config
        /* */, BlockingQueue<UpperProducerRecord> getout
        /* */, AtomicReference<SimpleStatus> status
        /* */)
    {
        super(config, getout, status);
    }

    @Override
    protected void runInternal(UpperProducer worker, TripleProducerMetric metric) //
        throws Exception
    {
        try (Producer<byte[], byte[]> kfk = this.producer(BYTES, BYTES)) {
            UpperProducerRecord d = null;
            while (super.newStatus(worker, metric) instanceof SimpleStatusInnerRun) {
                if (d == null) {
                    d = this.poll(metric);
                }
                else {
                    String keyText = d.key.toObjectNode().toString();
                    String valText = d.val.toObjectNode().toString();
                    String t = this.config.targetTopic;
                    int p = this.config.partitionNo;
                    byte[] k = keyText.getBytes(StandardCharsets.UTF_8);
                    byte[] v = valText.getBytes(StandardCharsets.UTF_8);
                    ProducerRecord<byte[], byte[]> r //
                        = new ProducerRecord<>(t, p, k, v);
                    LogSequenceNumber lsn = LogSequenceNumber.INVALID_LSN;
                    if (d.val instanceof PgsqlValTxactCommit) {
                        PgsqlValTxactCommit val = (PgsqlValTxactCommit)d.val;
                        lsn = LogSequenceNumber.valueOf(val.lsnofmsg);
                    }
                    SimpleHolder holder = SimpleHolder.of(this.status);
                    kfk.send(r, UpperKafkaProducerCallback.of(lsn, holder, kfk));
                    d = null;
                }
            }
        }
    }

    @Override
    public void runWithInterrupted() throws InterruptedException
    {
        ZonedDateTime startMillis = ZonedDateTime.now();
        TripleProducerMetric metric = TripleProducerMetric.of(startMillis);
        super.run("upper-producer", this, metric);
    }
}
