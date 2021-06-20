package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Xqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordProducer;
import com.hktcode.simple.SimpleWorkerGauges;
import org.postgresql.replication.LogSequenceNumber;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class UppdcWorkerGauges extends SimpleWorkerGauges
{
    public static UppdcWorkerGauges of(UppdcWorkerArgval argval, AtomicLong xidlsn)
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        if (xidlsn == null) {
            throw new ArgumentNullException("xidlsn");
        }
        return new UppdcWorkerGauges(argval, xidlsn);
    }

    private UppdcWorkerGauges(UppdcWorkerArgval argval, AtomicLong xidlsn)
    {
        this.txactionLsn = xidlsn;
        this.fetchMetric = argval.fetchXqueue.fetchXqueue();
        this.spinsMetric = argval.spinsArgval;
    }

    public final AtomicLong txactionLsn;

    public final AtomicReference<Throwable> callbackRef = new AtomicReference<>();

    public final Xqueue.Fetch<UpperRecordProducer> fetchMetric;

    public final Xqueue.Spins spinsMetric;

    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node = super.toJsonObject(node);
        LogSequenceNumber lsn = LogSequenceNumber.valueOf(txactionLsn.get());
        node.put("txaction_lsn", lsn.asString());
        return node;
    }
}
