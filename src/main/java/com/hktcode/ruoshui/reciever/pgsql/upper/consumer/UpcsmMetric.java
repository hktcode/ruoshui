package com.hktcode.ruoshui.reciever.pgsql.upper.consumer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.simple.SimpleMetric;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

import java.util.concurrent.atomic.AtomicLong;

public class UpcsmMetric extends SimpleMetric
{
    public static UpcsmMetric of()
    {
        return new UpcsmMetric();
    }

    private UpcsmMetric()
    {
    }

    public final AtomicLong txactionLsn = new AtomicLong(0);

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
