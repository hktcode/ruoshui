/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;

public class UpperRecordConsumer
{
    public static UpperRecordConsumer of(long lsn, LogicalMsg msg)
    {
        if (msg == null) {
            throw new ArgumentNullException("msg");
        }
        return new UpperRecordConsumer(lsn, msg);
    }

    public final long lsn;

    public final LogicalMsg msg;

    private UpperRecordConsumer(long lsn, LogicalMsg msg)
    {
        this.lsn = lsn;
        this.msg = msg;
    }
}
