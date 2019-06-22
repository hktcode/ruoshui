/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.datatype;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalDatatypeInfMsg;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerMutableMetric;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;

public class UpperDatatypeRecordNormal implements UpperDatatypeRecord
{
    public static UpperDatatypeRecordNormal of(long lsn, LogicalDatatypeInfMsg msg)
    {
        if (msg == null) {
            throw new ArgumentNullException("msg");
        }
        return new UpperDatatypeRecordNormal(lsn, msg);
    }

    public final long lsn;

    public final LogicalDatatypeInfMsg msg;

    private UpperDatatypeRecordNormal(long lsn, LogicalDatatypeInfMsg msg)
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
