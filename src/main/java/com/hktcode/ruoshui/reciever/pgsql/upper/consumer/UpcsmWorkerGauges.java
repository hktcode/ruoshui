package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.queue.Xqueue;
import com.hktcode.ruoshui.reciever.pgsql.upper.UpperRecordConsumer;
import com.hktcode.simple.SimpleWorkerGauges;
import org.postgresql.replication.LogSequenceNumber;

import java.util.concurrent.atomic.AtomicLong;

public class UpcsmWorkerGauges extends SimpleWorkerGauges
{
    public static UpcsmWorkerGauges of(UpcsmWorkerArgval argval, AtomicLong xidlsn)
    {
        if (argval == null) {
            throw new ArgumentNullException("argval");
        }
        if (xidlsn == null) {
            throw new ArgumentNullException("xidlsn");
        }
        return new UpcsmWorkerGauges(argval, xidlsn);
    }

    private UpcsmWorkerGauges(UpcsmWorkerArgval argval, AtomicLong xidlsn)
    {
        this.txactionLsn = xidlsn;
        this.offerMetric = argval.sender.offerXqueue();
        this.spinsMetric = argval.xspins;
    }

    public final AtomicLong txactionLsn;
    public long reportedLsn = 0;
    public final Xqueue.Offer<UpperRecordConsumer> offerMetric;
    public final Xqueue.Spins spinsMetric;

    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        node = super.toJsonObject(node);
        LogSequenceNumber lsn = LogSequenceNumber.valueOf(this.reportedLsn);
        node.put("reported_lsn", lsn.asString());
        return node;
    }
}
