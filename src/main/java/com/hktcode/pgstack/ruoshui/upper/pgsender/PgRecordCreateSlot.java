/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.lang.exception.NeverHappenAssertionError;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.consumer.*;

public class PgRecordCreateSlot implements PgRecord
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
    public UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmSenderSnapshotSimpleData sender)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (sender == null) {
            throw new ArgumentNullException("thread");
        }
        action.fetchThread = UpcsmSenderSnapshotUntilPoint.of(sender, slotTuple);
        return null;
    }

    @Override
    public UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmSenderMainline sender)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (sender == null) {
            throw new ArgumentNullException("sender");
        }
        return null;
    }
}
