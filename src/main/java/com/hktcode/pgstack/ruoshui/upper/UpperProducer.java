/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper;

import com.hktcode.bgtriple.naive.NaiveProducerMutableMetric;
import com.hktcode.bgtriple.status.TripleBasicBgStatus;
import com.hktcode.bgtriple.status.TripleRunBgStatus;
import com.hktcode.bgtriple.kafka.KafkaCallbackProducer;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgsqlValTxactCommit;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperProducerConfig;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperProducerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.postgresql.replication.LogSequenceNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import static com.hktcode.kafka.Kafka.Serializers.BYTES;

public class UpperProducer extends KafkaCallbackProducer
    /* */< UpperConsumer
    /* */, UpperJunction
    /* */, UpperProducer
    /* */, UpperProducerConfig
    /* */, NaiveProducerMutableMetric
    /* */, UpperProducerRecord
    /* */, byte[]
    /* */, byte[]
    /* */>
{
    public static UpperProducer of//
        /* */( UpperProducerConfig config
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status
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

    private static final Logger logger = LoggerFactory.getLogger(UpperProducer.class);

    private UpperProducer //
        /* */( UpperProducerConfig config
        /* */, BlockingQueue<UpperProducerRecord> getout
        /* */, AtomicReference<TripleBasicBgStatus<UpperConsumer, UpperJunction, UpperProducer>> status
        /* */)
    {
        super(config, NaiveProducerMutableMetric.of(), getout, status);
    }

    @Override
    public void runInternal() throws InterruptedException
    {
        try (Producer<byte[], byte[]> kfk = this.producer(BYTES, BYTES)) {
            UpperProducerRecord d = null;
            while (super.newStatus() instanceof TripleRunBgStatus) {
                if (d == null) {
                    d = this.poll();
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
                    kfk.send(r, UpperKafkaProducerCallback.of(lsn, super.switcher(), kfk));
                    d = null;
                }
            }
        }
    }
}
