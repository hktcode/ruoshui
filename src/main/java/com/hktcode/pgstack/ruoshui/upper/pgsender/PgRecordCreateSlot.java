/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmActionRun;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmFetchRecordSnapshot;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmThreadSnapshot;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmThreadSnapshotUntilPoint;

public class PgRecordCreateSlot implements UpcsmFetchRecordSnapshot
{
    public static PgRecordCreateSlot of(PgReplSlotTuple slotTuple)
    {
        if (slotTuple == null) {
            throw new ArgumentNullException("slotTuple");
        }
        return new PgRecordCreateSlot(slotTuple);
    }

    public final PgReplSlotTuple slotTuple;

    private PgRecordCreateSlot(PgReplSlotTuple slotTuple)
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
