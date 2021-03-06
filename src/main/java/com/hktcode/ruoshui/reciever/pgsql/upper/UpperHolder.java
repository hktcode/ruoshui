package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Tqueue;
import com.hktcode.queue.TqueueMetric;
import com.hktcode.ruoshui.reciever.pgsql.upper.consumer.UpcsmMetric;
import com.hktcode.ruoshui.reciever.pgsql.upper.consumer.UpcsmThread;
import com.hktcode.ruoshui.reciever.pgsql.upper.junction.UpjctMetric;
import com.hktcode.ruoshui.reciever.pgsql.upper.junction.UpjctThread;
import com.hktcode.ruoshui.reciever.pgsql.upper.producer.UppdcMetric;
import com.hktcode.ruoshui.reciever.pgsql.upper.producer.UppdcThread;
import com.hktcode.ruoshui.reciever.pgsql.upper.storeman.UpperKeeperOnlyone;
import com.hktcode.simple.SimplePhaserOuter;
import com.hktcode.simple.SimpleEntity;
import org.postgresql.replication.LogSequenceNumber;

import java.util.concurrent.atomic.AtomicLong;

public class UpperHolder extends SimpleEntity<UpperResult>
{
    public static UpperHolder of(long createts, String fullname, UpperConfig config, UpperKeeperOnlyone storeman)
    {
        if (fullname == null) {
            throw new ArgumentNullException("fullname");
        }
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (storeman == null) {
            throw new ArgumentNullException("storeman");
        }
        return new UpperHolder(createts, fullname, config, storeman);
    }

    public final long createts;
    public final String fullname;
    private final UpcsmThread consumer; // laborer
    public final Tqueue<UpperRecordConsumer> srcqueue;
    private final UpjctThread junction;
    public final Tqueue<UpperRecordProducer> tgtqueue;
    private final UppdcThread producer;
    private final UpperKeeperOnlyone storeman;

    private UpperHolder //
        /* */( long createts //
        /* */, String fullname //
        /* */, UpperConfig config //
        /* */, UpperKeeperOnlyone storeman //
        /* */)
    {
        this.createts = createts;
        this.fullname = fullname;
        AtomicLong txactionLsn = new AtomicLong(LogSequenceNumber.INVALID_LSN.asLong());
        this.consumer = UpcsmThread.of(config.consumer, UpcsmMetric.of(txactionLsn), this);
        this.srcqueue = Tqueue.of(config.srcqueue, TqueueMetric.of());
        this.junction = UpjctThread.of(config.junction, UpjctMetric.of(), this);
        this.tgtqueue = Tqueue.of(config.tgtqueue, TqueueMetric.of());
        this.producer = UppdcThread.of(config.producer, UppdcMetric.of(txactionLsn), this);
        this.storeman = storeman;
    }

    private UpperResult end(long deletets)
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
        return this.toResultNode(deletets);
    }

    @Override
    public UpperResult end(SimplePhaserOuter cmd) throws InterruptedException
    {
        if (cmd == null) {
            throw new ArgumentNullException("cmd");
        }
        return this.end(cmd, (o, f)->this.end(o.deletets));
    }

    public UpperResult get(SimplePhaserOuter cmd) throws InterruptedException
    {
        if (cmd == null) {
            throw new ArgumentNullException("cmd");
        }
        return this.run(cmd, (o, f)->this.toResultNode(o.deletets));
    }

    public UpperResult put(SimplePhaserOuter cmd) throws InterruptedException
    {
        this.producer.setName(this.fullname + "-upper-producer");
        this.producer.start();
        this.junction.setName(this.fullname + "-upper-junction");
        this.junction.start();
        this.consumer.setName(this.fullname + "-upper-consumer");
        this.consumer.start();
        return this.run(cmd, (o, f)->this.put(o.deletets));
    }

    private UpperResult put(long deletets)
    {
        ObjectNode node = this.storeman.mapper.createObjectNode();
        this.toConfigNode(node);
        this.storeman.updertYml(this.fullname, node);
        return this.toResultNode(deletets);
    }

    public UpperResult del(SimplePhaserOuter cmd) throws InterruptedException
    {
        return this.run(cmd, (o, f)->this.del(o.deletets));
    }

    private UpperResult del(long deletets)
    {
        ObjectNode node = this.storeman.mapper.createObjectNode();
        this.toConfigNode(node);
        deletets = this.storeman.deleteYml(this.fullname, node, deletets);
        return this.toResultNode(deletets);
    }

    public UpperResult pst(SimplePhaserOuter cmd, JsonNode json) throws InterruptedException
    {
        if (json == null) {
            throw new ArgumentNullException("node");
        }
        return this.run(cmd, (o, f)->this.pst(json));
    }

    private UpperResult pst(JsonNode node)
    {
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
        ObjectNode conf = this.toConfigNode(this.storeman.mapper.createObjectNode());
        this.storeman.updertYml(this.fullname, conf);
        return this.toResultNode(Long.MAX_VALUE);
    }

    private ObjectNode toConfigNode(ObjectNode node)
    {
        ObjectNode consumer = node.putObject("consumer");
        ObjectNode srcqueue = node.putObject("srcqueue");
        ObjectNode junction = node.putObject("junction");
        ObjectNode tgtqueue = node.putObject("tgtqueue");
        ObjectNode producer = node.putObject("producer");
        this.consumer.config.toJsonObject(consumer);
        this.srcqueue.config.toJsonObject(srcqueue);
        this.junction.config.toJsonObject(junction);
        this.tgtqueue.config.toJsonObject(tgtqueue);
        this.producer.config.toJsonObject(producer);
        return node;
    }

    private UpperResult toResultNode(long deletets)
    {
        long createts = this.createts;
        String fullname = this.fullname;
        ObjectNode consumer = this.consumer.toJsonObject();
        ObjectNode srcqueue = this.srcqueue.toJsonObject();
        ObjectNode junction = this.junction.toJsonObject();
        ObjectNode tgtqueue = this.tgtqueue.toJsonObject();
        ObjectNode producer = this.producer.toJsonObject();
        return UpperResult.of(createts, fullname, consumer, srcqueue, junction, tgtqueue, producer, deletets);
    }
}
