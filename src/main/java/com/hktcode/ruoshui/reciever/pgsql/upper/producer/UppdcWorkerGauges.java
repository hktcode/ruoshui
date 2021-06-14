package com.hktcode.ruoshui.reciever.pgsql.upper.producer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.simple.SimpleWorkerGauges;
import org.postgresql.replication.LogSequenceNumber;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class UppdcWorkerGauges extends SimpleWorkerGauges
{
    public static UppdcWorkerGauges of(AtomicLong txactionLsn)
    {
        if (txactionLsn == null) {
            throw new ArgumentNullException("txactionLsn");
        }
        return new UppdcWorkerGauges(txactionLsn);
    }

    private UppdcWorkerGauges(AtomicLong txactionLsn)
    {
        this.txactionLsn = txactionLsn;
    }

    public final AtomicLong txactionLsn;

    public final AtomicReference<Throwable> callbackRef = new AtomicReference<>();

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
