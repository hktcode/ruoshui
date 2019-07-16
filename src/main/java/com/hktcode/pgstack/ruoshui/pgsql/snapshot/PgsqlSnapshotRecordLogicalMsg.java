/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;

public class PgsqlSnapshotRecordLogicalMsg implements PgsqlSnapshotRecord
{
    public static PgsqlSnapshotRecordLogicalMsg of(long lsn, LogicalMsg msg)
    {
        if (msg == null) {
            throw new ArgumentNullException("msg");
        }
        return new PgsqlSnapshotRecordLogicalMsg(lsn, msg);
    }

    public final long lsn;

    public final LogicalMsg msg;

    private PgsqlSnapshotRecordLogicalMsg(long lsn, LogicalMsg msg)
    {
        this.lsn = lsn;
        this.msg = msg;
    }
}
