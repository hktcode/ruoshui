/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.pgsql.snapshot;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.pgsql.PgReplSlotTuple;

public class PgsqlSnapshotRecordCreateSlot implements PgsqlSnapshotRecord
{
    public static PgsqlSnapshotRecordCreateSlot of(PgReplSlotTuple slotTuple)
    {
        if (slotTuple == null) {
            throw new ArgumentNullException("slotTuple");
        }
        return new PgsqlSnapshotRecordCreateSlot(slotTuple);
    }

    public final PgReplSlotTuple slotTuple;

    private PgsqlSnapshotRecordCreateSlot(PgReplSlotTuple slotTuple)
    {
        this.slotTuple = slotTuple;
    }
}
