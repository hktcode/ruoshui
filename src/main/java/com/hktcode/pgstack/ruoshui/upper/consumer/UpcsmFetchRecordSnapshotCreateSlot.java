/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;

public class UpcsmFetchRecordSnapshotCreateSlot implements UpcsmFetchRecordSnapshot
{
    public static UpcsmFetchRecordSnapshotCreateSlot of(PgReplSlotTuple slotTuple)
    {
        if (slotTuple == null) {
            throw new ArgumentNullException("slotTuple");
        }
        return new UpcsmFetchRecordSnapshotCreateSlot(slotTuple);
    }

    public final PgReplSlotTuple slotTuple;

    private UpcsmFetchRecordSnapshotCreateSlot(PgReplSlotTuple slotTuple)
    {
        this.slotTuple = slotTuple;
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
        action.fetchThread = UpcsmThreadSnapshotUntilPoint.of(thread, slotTuple);
        return null;
    }
}
