/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;

class PgsqlTxactionRecordNormal implements PgsqlTxactionRecord
{
    static PgsqlTxactionRecordNormal of(long lsn, LogicalMsg msg)
    {
        if (msg == null) {
            throw new ArgumentNullException("msg");
        }
        return new PgsqlTxactionRecordNormal(lsn, msg);
    }

    private final long lsn;

    private final LogicalMsg msg;

    private PgsqlTxactionRecordNormal(long lsn, LogicalMsg msg)
    {
        this.lsn = lsn;
        this.msg = msg;
    }

    @Override
    public UpperConsumerRecord update(UpperConsumerMetric metric)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return UpperConsumerRecord.of(this.lsn, this.msg);
    }
}
