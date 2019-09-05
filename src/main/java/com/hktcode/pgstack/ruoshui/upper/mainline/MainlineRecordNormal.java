/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerMetric;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;

class MainlineRecordNormal implements MainlineRecord
{
    static MainlineRecordNormal of(long lsn, LogicalMsg msg)
    {
        if (msg == null) {
            throw new ArgumentNullException("msg");
        }
        return new MainlineRecordNormal(lsn, msg);
    }

    public final long lsn;

    public final LogicalMsg msg;

    private MainlineRecordNormal(long lsn, LogicalMsg msg)
    {
        this.lsn = lsn;
        this.msg = msg;
    }

    @Override
    public UpperConsumerRecord toUpcsmRecord()
    {
        return UpperConsumerRecord.of(this.lsn, this.msg);
    }
}
