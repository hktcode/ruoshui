/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgjdbc.LogicalTxactBeginsMsg;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpcsmThreadSnapshotUntilPoint extends UpcsmThreadSnapshot
{
    private static final Logger logger //
        = LoggerFactory.getLogger(UpcsmThreadSnapshotUntilPoint.class);

    public static UpcsmThreadSnapshotUntilPoint of
        /* */( UpcsmThreadSnapshot snapshot //
        /* */, PgReplSlotTuple slttuple //
        /* */) //
    {
        if (snapshot == null) {
            throw new ArgumentNullException("snapshot");
        }
        if (slttuple == null) {
            throw new ArgumentNullException("slttuple");
        }
        return new UpcsmThreadSnapshotUntilPoint(snapshot, slttuple);
    }

    private final PgReplSlotTuple slot;

    private UpcsmThreadSnapshotUntilPoint //
        /* */( UpcsmThreadSnapshot snapshot //
        /* */, PgReplSlotTuple slttuple //
        /* */) //
    {
        super(snapshot.mlxact, snapshot.thread, snapshot.tqueue, snapshot.status);
        this.slot = slttuple;
    }

    @Override
    public UpperRecordConsumer poll(long timeout, UpcsmActionRun action) //
        throws InterruptedException
    {
        UpperRecordConsumer record = mlxact.poll(timeout, action);
        if (record != null) {
            if (record.msg instanceof LogicalTxactBeginsMsg) {
                LogicalTxactBeginsMsg beginsMsg = (LogicalTxactBeginsMsg)record.msg;
                if (Long.compareUnsigned(beginsMsg.lsnofcmt, slot.consistentPoint) > 0) {
                    action.fetchThread = UpcsmThreadSnapshotSelectData.of(this, record);
                    return null;
                }
            }
            return record;
        }
        else if (thread.isAlive()) {
            return null;
        }
        else {
            logger.error("snapshot post is not alive."); // TODO:
            // TODO: mlxact.sslist.add();
            action.fetchThread = mlxact;
            return null;
        }
    }
}
