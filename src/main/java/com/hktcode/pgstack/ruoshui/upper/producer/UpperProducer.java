/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.bgsimple.triple.TripleProducerMetric;
import com.hktcode.bgsimple.triple.kafka.KafkaTripleProducer;
import com.hktcode.lang.RunnableWithInterrupted;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgsqlValTxactCommit;
import com.hktcode.pgstack.ruoshui.upper.UpperKafkaProducerCallback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.postgresql.replication.LogSequenceNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger logger = LoggerFactory.getLogger(UpperProducer.class);

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
    protected void runInternal(UpperProducer worker) //
        throws Exception
    {
        try (Producer<byte[], byte[]> kfk = this.producer(BYTES, BYTES)) {
            logger.info("kfk.metrics={}", kfk.metrics());
            logger.info("target_topic={}, partition_no={}", config.targetTopic, config.partitionNo);
            UpperProducerRecord d = null;
            while (super.newStatus(worker) instanceof SimpleStatusInnerRun) {
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
        // TripleProducerMetric metric = TripleProducerMetric.of(startMillis);
        super.run("upper-producer", this);
    }

    @Override
    public JsonNode toJsonObject()
    {
        // TODO:
        return new ObjectNode(JsonNodeFactory.instance);
    }
}
