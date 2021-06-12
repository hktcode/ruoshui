package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Tqueue;
import com.hktcode.queue.TqueueMetric;
import com.hktcode.ruoshui.reciever.pgsql.upper.consumer.UpcsmWorker;
import com.hktcode.ruoshui.reciever.pgsql.upper.junction.UpjctWorker;
import com.hktcode.ruoshui.reciever.pgsql.upper.producer.UppdcWorker;
import com.hktcode.simple.SimpleAtomic;

public class UpperExesvc
{
    public static UpperExesvc of(UpperExesvcArgval config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return new UpperExesvc(config);
    }

    private final UpperExesvcArgval argval;
    private final UpperExesvcGauges gauges;
    private final UpperQueues queues;
    private final SimpleAtomic holder;

    private UpperExesvc(UpperExesvcArgval argval)
    {
        this.argval = argval;
        this.gauges = UpperExesvcGauges.of();
        Tqueue<UpperRecordConsumer> source = Tqueue.of(argval.srcqueue, TqueueMetric.of());
        Tqueue<UpperRecordProducer> target = Tqueue.of(argval.tgtqueue, TqueueMetric.of());
        this.queues = UpperQueues.of(source, target);
        this.holder = SimpleAtomic.of();
    }

    public UpcsmWorker consumer()
    {
        return UpcsmWorker.of(this.argval.consumer, this.gauges.consumer, this.holder, this.queues.source);
    }

    public UpjctWorker junction()
    {
        return UpjctWorker.of(this.argval.junction, this.gauges.junction, this.holder, this.queues);
    }

    public UppdcWorker producer()
    {
        return UppdcWorker.of(this.argval.producer, this.gauges.producer, this.holder, this.queues.target);
    }

    public UpperResult modify(long finishts, JsonNode jsonnode, SimpleKeeper storeman)
            throws InterruptedException
    {
        if (jsonnode == null) {
            throw new ArgumentNullException("node");
        }
        if (storeman == null) {
            throw new ArgumentNullException("storeman");
        }
        return this.holder.call((d)->this.modify(d, finishts, jsonnode, storeman));
    }

    private UpperResult modify(long deletets, long finishts, JsonNode jsonnode, SimpleKeeper storeman)
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
        if (deletets == Long.MAX_VALUE) {
            deletets = finishts;
        }
        storeman.call(this.argval);
        long createts = this.gauges.createts;
        String fullname = this.argval.fullname;
        ObjectNode consumer = this.argval.consumer.toJsonObject();
        ObjectNode srcqueue = this.argval.srcqueue.toJsonObject();
        ObjectNode junction = this.argval.junction.toJsonObject();
        ObjectNode tgtqueue = this.argval.tgtqueue.toJsonObject();
        ObjectNode producer = this.argval.producer.toJsonObject();
        return UpperResult.of(createts, fullname, consumer, srcqueue, junction, tgtqueue, producer, deletets);
    }

    @FunctionalInterface
    public interface SimpleKeeper
    {
        void call(UpperExesvcArgval argval);
    }
}
