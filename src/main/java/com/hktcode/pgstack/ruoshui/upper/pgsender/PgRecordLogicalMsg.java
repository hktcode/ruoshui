/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalMsg;
import com.hktcode.pgjdbc.LogicalTxactBeginsMsg;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.consumer.*;

public class PgRecordLogicalMsg implements PgRecord
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
    public UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmSenderSnapshotUntilPoint sender)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        UpperRecordConsumer result = UpperRecordConsumer.of(lsn, msg);
        if (!(this.msg instanceof LogicalTxactBeginsMsg)) {
            return result;
        }
        LogicalTxactBeginsMsg beginsMsg = (LogicalTxactBeginsMsg)this.msg;
        long consistentPoint = sender.slot.consistentPoint;
        if (Long.compareUnsigned(beginsMsg.lsnofcmt, consistentPoint) > 0) {
            action.fetchThread = UpcsmSenderSnapshotSimpleData.of(sender, result);
            return null;
        }
        return result;
    }

    @Override
    public UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmSenderSnapshotSimpleData thread)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (thread == null) {
            throw new ArgumentNullException("thread");
        }
        return UpperRecordConsumer.of(lsn, msg);
    }

    @Override
    public UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmSenderMainline thread)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (thread == null) {
            throw new ArgumentNullException("thread");
        }
        return UpperRecordConsumer.of(this.lsn, msg);
    }
}
