package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.simple.SimpleWorkerMeters;
import org.postgresql.replication.LogSequenceNumber;

import java.util.concurrent.atomic.AtomicLong;

public class UpcsmWorkerMeters extends SimpleWorkerMeters
{
    public static UpcsmWorkerMeters of(AtomicLong txactionLsn)
    {
        if (txactionLsn == null) {
            throw new ArgumentNullException("txactionLsn");
        }
        return new UpcsmWorkerMeters(txactionLsn);
    }

    private UpcsmWorkerMeters(AtomicLong txactionLsn)
    {
        this.txactionLsn = txactionLsn;
    }

    public final AtomicLong txactionLsn;
    public long reportedLsn = 0;

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
