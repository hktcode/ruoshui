package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.simple.SimpleMeters;
import com.hktcode.simple.SimpleMetric;
import org.postgresql.replication.LogSequenceNumber;

import java.util.concurrent.atomic.AtomicLong;

public class UpcsmMeters extends SimpleMeters
{
    public static UpcsmMeters of(AtomicLong txactionLsn)
    {
        if (txactionLsn == null) {
            throw new ArgumentNullException("txactionLsn");
        }
        return new UpcsmMeters(txactionLsn);
    }

    private UpcsmMeters(AtomicLong txactionLsn)
    {
        this.txactionLsn = txactionLsn;
    }

    public final AtomicLong txactionLsn;

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
