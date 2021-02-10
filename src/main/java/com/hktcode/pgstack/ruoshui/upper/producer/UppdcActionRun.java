/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.producer;

import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.status.SimpleStatusRun;
import com.hktcode.bgsimple.triple.TripleAction;
import com.hktcode.bgsimple.triple.TripleActionEnd;
import com.hktcode.bgsimple.triple.TripleActionRun;
import com.hktcode.bgsimple.triple.TripleMetricEnd;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgsqlValTxactCommit;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.postgresql.replication.LogSequenceNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import static com.hktcode.kafka.Kafka.Serializers.BYTES;

class UppdcActionRun extends TripleActionRun<UppdcConfig, UppdcMetricRun>
{
    public static UppdcActionRun of
        /* */( UppdcConfig config
        /* */, BlockingQueue<UpperRecordProducer> getout
        /* */, SimpleHolder status
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
        return new UppdcActionRun(config, getout, status);
    }

    private static final Logger logger = LoggerFactory.getLogger(UppdcActionRun.class);

    private final BlockingQueue<UpperRecordProducer> getout;

    private UppdcActionRun
        /* */( UppdcConfig config
        /* */, BlockingQueue<UpperRecordProducer> getout
        /* */, SimpleHolder status
        /* */)
    {
        super(status, config, 2);
        this.getout = getout;
    }

    @Override
    public TripleAction<UppdcConfig, UppdcMetricRun> next() throws InterruptedException
    {
        Properties properties = new Properties();
        for (Map.Entry<String, String> e : config.kfkProperty.entrySet()) {
            properties.setProperty(e.getKey(), e.getValue());
        }
        try (Producer<byte[], byte[]> kfk = new KafkaProducer<>(properties, BYTES, BYTES)) {
            logger.info("kfk.metrics={}", kfk.metrics());
            logger.info("target_topic={}, partition_no={}", config.targetTopic, config.partitionNo);
            UpperRecordProducer d = null;
            while (this.status.run(this, this.number) instanceof SimpleStatusRun) {
                if (d == null) {
                    d = this.poll(getout);
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
                    kfk.send(r, UppdcKafkaCallback.of(lsn, this.status, kfk));
                    d = null;
                }
            }
        }
        UppdcMetricRun basicMetric = this.toRunMetrics();
        TripleMetricEnd<UppdcMetricRun> metric = TripleMetricEnd.of(basicMetric);
        return TripleActionEnd.of(this, config, metric, this.number);
    }

    @Override
    public UppdcMetricRun toRunMetrics()
    {
        return UppdcMetricRun.of(this);
    }
}
