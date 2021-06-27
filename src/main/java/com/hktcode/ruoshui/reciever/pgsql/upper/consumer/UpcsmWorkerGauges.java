package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Xqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordConsumer;
import com.hktcode.simple.SimpleWorkerGauges;

import java.util.concurrent.atomic.AtomicLong;

public class UpcsmWorkerGauges extends SimpleWorkerGauges
{
    public static UpcsmWorkerGauges of(UpcsmWorkerArgval argval)
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        return new UpcsmWorkerGauges(argval);
    }

    private UpcsmWorkerGauges(UpcsmWorkerArgval argval)
    {
        this.recver = argval.recver;
        this.xspins = argval.xspins;
        this.sender = argval.sender.offerXqueue();
    }

    public final Xqueue.Spins xspins;
    public final Xqueue.Offer<UpperRecordConsumer> sender;
    public final UpcsmRecverArgval recver;

    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node = super.toJsonObject(node);
        return node;
    }
}
