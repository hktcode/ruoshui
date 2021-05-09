package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Tqueue;
import com.hktcode.queue.TqueueMetric;
import com.hktcode.ruoshui.reciever.pgsql.upper.consumer.UpcsmWorker;
import com.hktcode.ruoshui.reciever.pgsql.upper.junction.UpjctWorker;
import com.hktcode.ruoshui.reciever.pgsql.upper.producer.UppdcWorker;
import com.hktcode.ruoshui.reciever.pgsql.upper.storeman.UpperKeeperOnlyone;
import com.hktcode.simple.SimpleExesvc;
import com.hktcode.simple.SimplePhaserOuter;

public class UpperExesvc extends SimpleExesvc
{
    public static UpperExesvc of(UpperExesvcArgval config, UpperKeeperOnlyone keeper)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        if (keeper == null) {
            throw new ArgumentNullException("keeper");
        }
        return new UpperExesvc(config, keeper);
    }

    private final UpperExesvcArgval argval;
    private final UpperExesvcGauges gauges;
    public final Tqueue<UpperRecordConsumer> srcqueue;
    public final Tqueue<UpperRecordProducer> tgtqueue;
    private final UpperKeeperOnlyone keeper;

    private UpperExesvc(UpperExesvcArgval argval, UpperKeeperOnlyone keeper)
    {
        this.argval = argval;
        this.keeper = keeper;
        this.gauges = UpperExesvcGauges.of();
        this.srcqueue = Tqueue.of(argval.srcqueue, TqueueMetric.of());
        this.tgtqueue = Tqueue.of(argval.tgtqueue, TqueueMetric.of());
    }

    @Override
    public UpperResult end(SimplePhaserOuter cmd) throws InterruptedException
    {
        if (cmd == null) {
            throw new ArgumentNullException("cmd");
        }
        return this.end(cmd, (o, f)->this.end(o.deletets));
    }

    public UpperResult put(SimplePhaserOuter cmd) throws InterruptedException
    {
        if (cmd == null) {
            throw new ArgumentNullException("cmd");
        }
        this.submit(UppdcWorker.of(this.argval.producer, this.gauges.producer, this));
        this.submit(UpjctWorker.of(this.argval.junction, this.gauges.junction, this));
        this.submit(UpcsmWorker.of(this.argval.consumer, this.gauges.consumer, this));
        this.shutdown();
        return this.run(cmd, (o, f)->this.put(o.deletets));
    }

    public UpperResult del(SimplePhaserOuter cmd) throws InterruptedException
    {
        if (cmd == null) {
            throw new ArgumentNullException("cmd");
        }
        return this.run(cmd, (o, f)->this.del(o.deletets));
    }

    public UpperResult pst(SimplePhaserOuter cmd, JsonNode json) throws InterruptedException
    {
        if (json == null) {
            throw new ArgumentNullException("node");
        }
        return this.run(cmd, (o, f)->this.pst(json));
    }

    public UpperResult get(SimplePhaserOuter cmd) throws InterruptedException
    {
        if (cmd == null) {
            throw new ArgumentNullException("cmd");
        }
        return this.run(cmd, (o, f)->this.get(o.deletets));
    }

    private UpperResult end(long deletets)
    {
        if (deletets == Long.MAX_VALUE) {
            if (this.gauges.consumer.endDatetime == Long.MAX_VALUE) {
                this.gauges.consumer.endDatetime = this.gauges.consumer.exeDatetime;
            }
            if (this.gauges.junction.endDatetime == Long.MAX_VALUE) {
                this.gauges.junction.endDatetime = this.gauges.junction.exeDatetime;
            }
            if (this.gauges.producer.endDatetime == Long.MAX_VALUE) {
                this.gauges.producer.endDatetime = this.gauges.producer.exeDatetime;
            }
            deletets = System.currentTimeMillis();
        }
        return this.get(deletets);
    }

    private UpperResult put(long deletets)
    {
        ObjectNode node = this.keeper.mapper.createObjectNode();
        this.toConfigNode(node);
        this.keeper.updertYml(this.argval.fullname, node);
        return this.get(deletets);
    }

    private UpperResult del(long deletets)
    {
        ObjectNode node = this.keeper.mapper.createObjectNode();
        this.toConfigNode(node);
        deletets = this.keeper.deleteYml(this.argval.fullname, node, deletets);
        return this.get(deletets);
    }

    private UpperResult pst(JsonNode node)
    {
        JsonNode n;
        if ((n = node.get("consumer")) != null) {
            this.argval.consumer.pst(n);
        }
        if ((n = node.get("srcqueue")) != null) {
            this.argval.srcqueue.pst(n);
        }
        if ((n = node.get("junction")) != null) {
            this.argval.junction.pst(n);
        }
        if ((n = node.get("tgtqueue")) != null) {
            this.argval.tgtqueue.pst(n);
        }
        if ((n = node.get("producer")) != null) {
            this.argval.producer.pst(n);
        }
        ObjectNode conf = this.toConfigNode(this.keeper.mapper.createObjectNode());
        this.keeper.updertYml(this.argval.fullname, conf);
        return this.get(Long.MAX_VALUE);
    }

    private UpperResult get(long deletets)
    {
        long createts = this.gauges.createts;
        String fullname = this.argval.fullname;
        ObjectNode consumer = this.argval.consumer.toJsonObject();
        ObjectNode srcqueue = this.argval.srcqueue.toJsonObject();
        ObjectNode junction = this.argval.junction.toJsonObject();
        ObjectNode tgtqueue = this.argval.tgtqueue.toJsonObject();
        ObjectNode producer = this.argval.producer.toJsonObject();
        return UpperResult.of(createts, fullname, consumer, srcqueue, junction, tgtqueue, producer, deletets);
    }

    private ObjectNode toConfigNode(ObjectNode node)
    {
        ObjectNode consumer = node.putObject("consumer");
        ObjectNode srcqueue = node.putObject("srcqueue");
        ObjectNode junction = node.putObject("junction");
        ObjectNode tgtqueue = node.putObject("tgtqueue");
        ObjectNode producer = node.putObject("producer");
        this.argval.consumer.toJsonObject(consumer);
        this.srcqueue.config.toJsonObject(srcqueue);
        this.argval.junction.toJsonObject(junction);
        this.tgtqueue.config.toJsonObject(tgtqueue);
        this.argval.producer.toJsonObject(producer);
        return node;
    }
}
