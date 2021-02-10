package com.hktcode.ruoshui.reciever.pgsql.upper.junction;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.simple.SimpleMetric;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.ruoshui.reciever.pgsql.entity.LogicalTxactContext;
import org.postgresql.replication.LogSequenceNumber;

public class UpjctMetric extends SimpleMetric
{
    public static UpjctMetric of()
    {
        return new UpjctMetric();
    }

    private UpjctMetric()
    {
    }

    public long curLsnofcmt = 0;

    public long curSequence = 0;

    public final LogicalTxactContext txidContext = LogicalTxactContext.of();

    @Override
    public ObjectNode toJsonObject(ObjectNode node)
    {
        if (node == null) {
            throw new ArgumentNullException("node");
        }
        super.toJsonObject(node);
        node.put("cur_lsnofcmt", LogSequenceNumber.valueOf(curLsnofcmt).asString());
        node.put("cur_sequence", this.curSequence);
        return node;
    }
}
