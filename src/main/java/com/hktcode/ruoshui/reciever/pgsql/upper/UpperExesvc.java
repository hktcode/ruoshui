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

    public UpperResult end(JsonNode jsonnode) throws InterruptedException
    {
        if (jsonnode == null) {
            throw new ArgumentNullException("jsonnode");
        }
        long deletets = System.currentTimeMillis();
        return this.holder.call(deletets, (d)->this.end(d, jsonnode));
    }

    //        this.submit(UppdcWorker.of(this.argval.producer, this.gauges.producer, this));
    //        this.submit(UpjctWorker.of(this.argval.junction, this.gauges.junction, this));
    //        this.submit(UpcsmWorker.of(this.argval.consumer, this.gauges.consumer, this));
    //        this.shutdown();

    public UpperResult put(JsonNode jsonnode) throws InterruptedException
    {
        if (jsonnode == null) {
            throw new ArgumentNullException("jsonnode");
        }
        long deletets = Long.MAX_VALUE;
        return this.holder.call(deletets, (d)->this.put(d, jsonnode));
    }

    public UpperResult del(JsonNode jsonnode) throws InterruptedException
    {
        if (jsonnode == null) {
            throw new ArgumentNullException("jsonnode");
        }
        long deletets = System.currentTimeMillis();
        return this.holder.call(deletets, (d)->this.del(d, jsonnode));
    }

    public UpperResult pst(JsonNode jsonnode) throws InterruptedException
    {
        if (jsonnode == null) {
            throw new ArgumentNullException("node");
        }
        long deletets = Long.MAX_VALUE;
        return this.holder.call(deletets, (d)->this.pst(d, jsonnode));
    }

    public UpperResult get(JsonNode jsonnode) throws InterruptedException
    {
        if (jsonnode == null) {
            throw new ArgumentNullException("jsonnode");
        }
        long deletets = Long.MAX_VALUE;
        return this.holder.call(deletets, (d)->this.get(d, jsonnode));
    }

    private UpperResult end(long deletets, JsonNode jsonnode)
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
        return this.get(deletets, jsonnode);
    }

    private UpperResult put(long deletets, JsonNode jsonnode)
    {
        ObjectNode node = this.keeper.mapper.createObjectNode();
        this.toConfigNode(node);
        this.keeper.updertYml(this.argval.fullname, node);
        return this.get(deletets, jsonnode);
    }

    private UpperResult del(long deletets, JsonNode jsonnode)
    {
        ObjectNode node = this.keeper.mapper.createObjectNode();
        this.toConfigNode(node);
        deletets = this.keeper.deleteYml(this.argval.fullname, node, deletets);
        return this.get(deletets, jsonnode);
    }

    private UpperResult pst(long deletets, JsonNode jsonnode)
    {
        JsonNode n;
        if ((n = jsonnode.get("consumer")) != null) {
            this.argval.consumer.pst(n);
        }
        if ((n = jsonnode.get("srcqueue")) != null) {
            this.argval.srcqueue.pst(n);
        }
        if ((n = jsonnode.get("junction")) != null) {
            this.argval.junction.pst(n);
        }
        if ((n = jsonnode.get("tgtqueue")) != null) {
            this.argval.tgtqueue.pst(n);
        }
        if ((n = jsonnode.get("producer")) != null) {
            this.argval.producer.pst(n);
        }
        ObjectNode conf = this.toConfigNode(this.keeper.mapper.createObjectNode());
        this.keeper.updertYml(this.argval.fullname, conf);
        return this.get(deletets, jsonnode);
    }

    private UpperResult get(long deletets, JsonNode jsonnode)
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
