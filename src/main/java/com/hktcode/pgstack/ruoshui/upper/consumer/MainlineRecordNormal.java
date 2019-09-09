/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;

public class MainlineRecordNormal implements MainlineRecord
{
    public static MainlineRecordNormal of(long lsn, LogicalMsg msg)
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
    public UpperRecordConsumer toUpcsmRecord()
    {
        return UpperRecordConsumer.of(this.lsn, this.msg);
    }
}
