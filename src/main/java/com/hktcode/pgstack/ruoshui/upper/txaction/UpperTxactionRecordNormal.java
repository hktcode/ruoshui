/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.txaction;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;

public class UpperTxactionRecordNormal implements UpperTxactionRecord
{
    public static UpperTxactionRecordNormal of(long lsn, LogicalMsg msg)
    {
        if (msg == null) {
            throw new ArgumentNullException("msg");
        }
        return new UpperTxactionRecordNormal(lsn, msg);
    }

    public final long lsn;

    public final LogicalMsg msg;

    private UpperTxactionRecordNormal(long lsn, LogicalMsg msg)
    {
        this.lsn = lsn;
        this.msg = msg;
    }

    @Override
    public UpperConsumerRecord update(UpperConsumerMutableMetric metric)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return UpperConsumerRecord.of(this.lsn, this.msg);
    }
}
