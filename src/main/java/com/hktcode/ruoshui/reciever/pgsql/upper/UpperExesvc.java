package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Tqueue;
import com.hktcode.queue.TqueueMetric;
import com.hktcode.simple.SimpleExesvc;

public class UpperExesvc extends SimpleExesvc
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
    public final Tqueue<UpperRecordConsumer> srcqueue;
    public final Tqueue<UpperRecordProducer> tgtqueue;

    private UpperExesvc(UpperExesvcArgval argval)
    {
        this.argval = argval;
        this.gauges = UpperExesvcGauges.of();
        this.srcqueue = Tqueue.of(argval.srcqueue, TqueueMetric.of());
        this.tgtqueue = Tqueue.of(argval.tgtqueue, TqueueMetric.of());
    }

    //        this.submit(UppdcWorker.of(this.argval.producer, this.gauges.producer, this));
    //        this.submit(UpjctWorker.of(this.argval.junction, this.gauges.junction, this));
    //        this.submit(UpcsmWorker.of(this.argval.consumer, this.gauges.consumer, this));
    //        this.shutdown();

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
