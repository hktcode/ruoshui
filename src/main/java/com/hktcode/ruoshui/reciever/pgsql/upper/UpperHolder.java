package com.hktcode.ruoshui.reciever.pgsql.upper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.upper.consumer.UpcsmWorkerArgval;
import com.hktcode.ruoshui.reciever.pgsql.upper.consumer.UpcsmWorkerGauges;
import com.hktcode.ruoshui.reciever.pgsql.upper.junction.UpjctWorkerArgval;
import com.hktcode.ruoshui.reciever.pgsql.upper.junction.UpjctWorkerGauges;
import com.hktcode.ruoshui.reciever.pgsql.upper.producer.UppdcWorkerArgval;
import com.hktcode.ruoshui.reciever.pgsql.upper.producer.UppdcWorkerGauges;
import com.hktcode.simple.SimpleAtomic;
import com.hktcode.simple.SimpleWorker;

public class UpperHolder
{
    public static UpperHolder of(UpperHolderArgval argval)
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        return new UpperHolder(argval);
    }

    private final UpperHolderArgval argval;
    private final UpperHolderGauges gauges;
    private final SimpleAtomic atomic;

    private UpperHolder(UpperHolderArgval argval)
    {
        this.argval = argval;
        this.gauges = UpperHolderGauges.of();
        this.atomic = SimpleAtomic.of();
    }

    public SimpleWorker<UpcsmWorkerArgval, UpcsmWorkerGauges> consumer()
    {
        return SimpleWorker.of(this.argval.consumer, this.gauges.consumer, this.atomic);
    }

    public SimpleWorker<UpjctWorkerArgval, UpjctWorkerGauges> junction()
    {
        return SimpleWorker.of(this.argval.junction, this.gauges.junction, this.atomic);
    }

    public SimpleWorker<UppdcWorkerArgval, UppdcWorkerGauges> producer()
    {
        return SimpleWorker.of(this.argval.producer, this.gauges.producer, this.atomic);
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
        return this.atomic.call((d)->this.modify(d, finishts, jsonnode, storeman));
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
        void call(UpperHolderArgval argval);
    }
}
