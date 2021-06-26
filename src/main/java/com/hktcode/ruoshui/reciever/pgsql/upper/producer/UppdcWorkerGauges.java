package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Xqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import com.hktcode.simple.SimpleWorkerGauges;

public class UppdcWorkerGauges extends SimpleWorkerGauges
{
    public static UppdcWorkerGauges of(UppdcWorkerArgval argval)
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        return new UppdcWorkerGauges(argval);
    }

    private UppdcWorkerGauges(UppdcWorkerArgval argval)
    {
        this.recver = argval.recver.fetchXqueue();
        this.xspins = argval.xspins;
        this.sender = argval.sender;
    }

    public final Xqueue.Fetch<UpperRecordProducer> recver;

    public final Xqueue.Spins xspins;

    public final UppdcSender sender;

    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node = super.toJsonObject(node);
        // - LogSequenceNumber lsn = LogSequenceNumber.valueOf(xidlsn.get());
        // - node.put("txaction_lsn", lsn.asString());
        return node;
    }
}
