/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.entity;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;

public class UpperConsumerRecord
{
    public static UpperConsumerRecord of(long lsn, LogicalMsg msg)
    {
        if (msg == null) {
            throw new ArgumentNullException("msg");
        }
        return new UpperConsumerRecord(lsn, msg);
    }

    public final long lsn;

    public final LogicalMsg msg;

    private UpperConsumerRecord(long lsn, LogicalMsg msg)
    {
        this.lsn = lsn;
        this.msg = msg;
    }
}
