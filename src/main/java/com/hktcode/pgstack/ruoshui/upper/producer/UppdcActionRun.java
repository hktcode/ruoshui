/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.producer;

import com.hktcode.bgsimple.SimpleHolder;
import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgsqlValTxactCommit;
import com.hktcode.pgstack.ruoshui.upper.UpperKafkaProducerCallback;
import com.hktcode.pgstack.ruoshui.upper.UpperProducerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serializer;
import org.postgresql.replication.LogSequenceNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.hktcode.kafka.Kafka.Serializers.BYTES;

public class UppdcActionRun //
    extends SimpleWorker<UppdcAction> implements UppdcAction
{
    public static UppdcActionRun of
        /* */( UpperProducerConfig config
        /* */, BlockingQueue<UpperProducerRecord> getout
        /* */, AtomicReference<SimpleStatus> status
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

    public final UpperProducerConfig config;

    public final BlockingQueue<UpperProducerRecord> getout;

    public final long actionStart;

    public long recordCount = 0;

    public long fetchCounts = 0;

    public long fetchMillis = 0;

    public long offerCounts = 0;

    public long offerMillis = 0;

    public long logDatetime = 0;

    /**
     * 描述当前状态的信息.
     */
    public String statusInfor = "";

    private UppdcActionRun
        /* */( UpperProducerConfig config
        /* */, BlockingQueue<UpperProducerRecord> getout
        /* */, AtomicReference<SimpleStatus> status
        /* */)
    {
        super(status, 2);
        this.config = config;
        this.getout = getout;
        this.actionStart = System.currentTimeMillis();
    }

    @Override
    public UppdcActionErr next(Throwable throwsError)
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return UppdcActionErr.of(this, throwsError);
    }

    @Override
    public UppdcResult get()
    {
        UppdcMetricRun metric = UppdcMetricRun.of(this);
        return UppdcResultRun.of(config, metric);
    }

    @Override
    public UppdcResultEnd del()
    {
        UppdcMetricEnd metric = UppdcMetricEnd.of(this);
        return UppdcResultEnd.of(config, metric);
    }

    public UppdcAction next() throws InterruptedException, ScriptException
    {
        try (Producer<byte[], byte[]> kfk = this.producer(BYTES, BYTES)) {
            logger.info("kfk.metrics={}", kfk.metrics());
            logger.info("target_topic={}, partition_no={}", config.targetTopic, config.partitionNo);
            UpperProducerRecord d = null;
            while (super.newStatus(this) instanceof SimpleStatusInnerRun) {
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
        return UppdcActionEnd.of(this);
    }

    private Producer<byte[], byte[]> //
    producer(Serializer<byte[]> k, Serializer<byte[]> v)
    {
        Properties properties = new Properties();
        for (Map.Entry<String, String> e : config.kfkProperty.entrySet()) {
            properties.setProperty(e.getKey(), e.getValue());
        }
        return new KafkaProducer<>(properties, k, v);
    }

    private UpperProducerRecord poll() throws InterruptedException
    {
        long waitTimeout = config.waitTimeout;
        long logDuration = config.logDuration;
        long startsMillis = System.currentTimeMillis();
        UpperProducerRecord record = getout.poll(waitTimeout, TimeUnit.MILLISECONDS);
        long finishMillis = System.currentTimeMillis();
        this.fetchMillis += (finishMillis - startsMillis);
        ++fetchCounts;
        if (record == null) {
            long currMillis = System.currentTimeMillis();
            if (currMillis - logDatetime >= logDuration) {
                logger.info("poll record from getout timeout" //
                        + ": waitTimeout={}" //
                        + ", logDuration={}" //
                        + ", logDatetime={}" //
                        + ", currMillis={}" //
                    , waitTimeout, logDuration, logDatetime, currMillis);
                logDatetime = currMillis;
            }
        }
        return record;
    }
}
