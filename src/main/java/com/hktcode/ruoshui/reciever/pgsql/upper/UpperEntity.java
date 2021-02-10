package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Tqueue;
import com.hktcode.queue.TqueueMetric;
import com.hktcode.ruoshui.reciever.pgsql.upper.consumer.UpcsmEntity;
import com.hktcode.ruoshui.reciever.pgsql.upper.consumer.UpcsmMetric;
import com.hktcode.ruoshui.reciever.pgsql.upper.junction.UpjctEntity;
import com.hktcode.ruoshui.reciever.pgsql.upper.junction.UpjctMetric;
import com.hktcode.ruoshui.reciever.pgsql.upper.producer.UppdcEntity;
import com.hktcode.ruoshui.reciever.pgsql.upper.producer.UppdcMetric;

public class UpperEntity
{
    public static UpperEntity of(long createts, String fullname, UpperConfig config)
    {
        if (fullname == null) {
            throw new ArgumentNullException("fullname");
        }
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        UpcsmEntity consumer = UpcsmEntity.of(config.consumer, UpcsmMetric.of());
        Tqueue<UpperRecordConsumer> srcqueue = Tqueue.of(config.srcqueue, TqueueMetric.of());
        UpjctEntity junction = UpjctEntity.of(config.junction, UpjctMetric.of());
        Tqueue<UpperRecordProducer> tgtqueue = Tqueue.of(config.tgtqueue, TqueueMetric.of());
        UppdcEntity producer = UppdcEntity.of(config.producer, UppdcMetric.of());
        return new UpperEntity(createts, fullname, consumer, srcqueue, junction, tgtqueue, producer);
    }

    public final long createts;
    public final String fullname;
    public final UpcsmEntity consumer; // laborer
    public final Tqueue<UpperRecordConsumer> srcqueue;
    public final UpjctEntity junction;
    public final Tqueue<UpperRecordProducer> tgtqueue;
    public final UppdcEntity producer;

    private UpperEntity //
            /* */( long createts //
            /* */, String fullname //
            /* */, UpcsmEntity consumer //
            /* */, Tqueue<UpperRecordConsumer> srcqueue //
            /* */, UpjctEntity junction //
            /* */, Tqueue<UpperRecordProducer> tgtqueue //
            /* */, UppdcEntity producer //
            /* */)
    {
        this.createts = createts;
        this.fullname = fullname;
        this.consumer = consumer;
        this.srcqueue = srcqueue;
        this.junction = junction;
        this.tgtqueue = tgtqueue;
        this.producer = producer;
    }

    public UpperResult end(long deletets)
    {
        if (deletets == Long.MAX_VALUE) {
            if (this.consumer.metric.endDatetime == Long.MAX_VALUE) {
                this.consumer.metric.endDatetime = this.consumer.metric.exeDateTime;
            }
            if (this.junction.metric.endDatetime == Long.MAX_VALUE) {
                this.junction.metric.endDatetime = this.junction.metric.exeDateTime;
            }
            if (this.producer.metric.endDatetime == Long.MAX_VALUE) {
                this.producer.metric.endDatetime = this.producer.metric.exeDateTime;
            }
            deletets = System.currentTimeMillis();
        }
        return UpperResult.of(this, deletets);
    }

    public UpperResult get(long deletets)
    {
        return UpperResult.of(this, deletets);
    }

    public UpperResult pst(JsonNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        JsonNode n;
        if ((n = node.get("consumer")) != null) {
            this.consumer.pst(n);
        }
        if ((n = node.get("srcqueue")) != null) {
            this.srcqueue.pst(n);
        }
        if ((n = node.get("junction")) != null) {
            this.junction.pst(n);
        }
        if ((n = node.get("tgtqueue")) != null) {
            this.tgtqueue.pst(n);
        }
        if ((n = node.get("producer")) != null) {
            this.producer.pst(n);
        }
        return UpperResult.of(this, Long.MAX_VALUE);
    }
}
