package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.entity.PgsqlValTxactCommit;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.postgresql.replication.LogSequenceNumber;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

import static com.hktcode.kafka.Kafka.Serializers.BYTES;

public class UppdcSenderKafka extends UppdcSender<UppdcConfigKafka, UppdcMetricKafka>
{
    public static UppdcSenderKafka of(UppdcConfigKafka config, UppdcMetricKafka metric)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new UppdcSenderKafka(config, metric);
    }

    private final Producer<byte[], byte[]> handle;

    private UppdcSenderKafka(UppdcConfigKafka config, UppdcMetricKafka metric)
    {
        super(config, metric);
        Properties properties = new Properties();
        properties.setProperty("request.timeout.ms", "1000");
        for (Map.Entry<String, String> e : config.kfkProperty.entrySet()) {
            properties.setProperty(e.getKey(), e.getValue());
        }
        this.handle = new KafkaProducer<>(properties, BYTES, BYTES);
    }

    @Override
    public void send(UpperRecordProducer record)
    {
        String keyText = record.key.toJsonObject().toString();
        String valText = record.val.toJsonObject().toString();
        String t = config.targetTopic;
        int p = config.partitionNo;
        byte[] k = keyText.getBytes(StandardCharsets.UTF_8);
        byte[] v = valText.getBytes(StandardCharsets.UTF_8);
        ProducerRecord<byte[], byte[]> r = new ProducerRecord<>(t, p, k, v);
        LogSequenceNumber lsn = LogSequenceNumber.INVALID_LSN;
        if (record.val instanceof PgsqlValTxactCommit) {
            PgsqlValTxactCommit val = (PgsqlValTxactCommit)record.val;
            lsn = LogSequenceNumber.valueOf(val.lsnofmsg);
        }
        // TODO: kafka生产者的行为好奇怪
        this.handle.send(r, UppdcKafkaCallback.of(lsn, this.metric, this.handle));
    }

    @Override
    public void close()
    {
        this.handle.close();
    }
}
