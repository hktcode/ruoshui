/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmActionRun;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmFetchRecordSnapshot;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmThreadSnapshot;

public class PgRecordLogicalMsg implements UpcsmFetchRecordSnapshot
{
    public static PgRecordLogicalMsg of(long lsn, LogicalMsg msg)
    {
        if (msg == null) {
            throw new ArgumentNullException("msg");
        }
        return new PgRecordLogicalMsg(lsn, msg);
    }

    public final long lsn;

    public final LogicalMsg msg;

    private PgRecordLogicalMsg(long lsn, LogicalMsg msg)
    {
        this.lsn = lsn;
        this.msg = msg;
    }

    @Override
    public UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmThreadSnapshot thread)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (thread == null) {
            throw new ArgumentNullException("thread");
        }
        return UpperRecordConsumer.of(lsn, msg);
    }
}
