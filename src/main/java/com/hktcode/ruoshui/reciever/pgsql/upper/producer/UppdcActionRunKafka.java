/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Tqueue;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlValTxactCommit;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperHolder;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import com.hktcode.simple.SimpleAction;
import com.hktcode.simple.SimpleActionEnd;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.postgresql.replication.LogSequenceNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

import static com.hktcode.kafka.Kafka.Serializers.BYTES;

class UppdcActionRunKafka extends UppdcActionRun
{
    public static UppdcActionRunKafka of(UppdcConfigKafka config, UppdcMetric metric, UpperHolder holder)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (holder == null) {
            throw new ArgumentNullException("holder");
        }
        return new UppdcActionRunKafka(config, metric, holder);
    }

    private static final Logger logger = LoggerFactory.getLogger(UppdcActionRunKafka.class);

    private UppdcActionRunKafka(UppdcConfigKafka config, UppdcMetric metric, UpperHolder holder)
    {
        super(config, metric, holder);
    }

    @Override
    public SimpleAction<UppdcConfig, UppdcMetric, UpperHolder> next() throws Throwable
    {
        final Tqueue<UpperRecordProducer> getout = this.entity.tgtqueue;
        final UppdcConfigKafka config = (UppdcConfigKafka)this.config;
        Properties properties = new Properties();
        properties.setProperty("request.timeout.ms", "1000");
        for (Map.Entry<String, String> e : config.kfkProperty.entrySet()) {
            properties.setProperty(e.getKey(), e.getValue());
        }
        try (Producer<byte[], byte[]> kfk = new KafkaProducer<>(properties, BYTES, BYTES)) {
            logger.info("target_topic={}, partition_no={}, kfk.metrics={}",
                    config.targetTopic, config.partitionNo, kfk.metrics());
            UpperRecordProducer d = null;
            Throwable ex;
            while (this.entity.run(metric).deletets == Long.MAX_VALUE) {
                if ((ex = metric.callbackRef.get()) != null) {
                    throw ex;
                } else if (d == null) {
                    d = getout.poll();
                } else {
                    String keyText = d.key.toJsonObject().toString();
                    String valText = d.val.toJsonObject().toString();
                    String t = config.targetTopic;
                    int p = config.partitionNo;
                    byte[] k = keyText.getBytes(StandardCharsets.UTF_8);
                    byte[] v = valText.getBytes(StandardCharsets.UTF_8);
                    ProducerRecord<byte[], byte[]> r //
                            = new ProducerRecord<>(t, p, k, v);
                    LogSequenceNumber lsn = LogSequenceNumber.INVALID_LSN;
                    if (d.val instanceof PgsqlValTxactCommit) {
                        PgsqlValTxactCommit val = (PgsqlValTxactCommit)d.val;
                        lsn = LogSequenceNumber.valueOf(val.lsnofmsg);
                    }
                    // TODO: kafka生产者的行为好奇怪
                    kfk.send(r, UppdcKafkaCallback.of(lsn, metric, kfk));
                    d = null;
                }
            }
        }
        return SimpleActionEnd.of(this.config, this.metric, this.entity);
    }
}
