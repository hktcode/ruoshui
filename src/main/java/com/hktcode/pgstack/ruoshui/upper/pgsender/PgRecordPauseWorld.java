/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmActionRun;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmSenderSnapshot;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmSenderSnapshotCreateSlot;

public class PgRecordPauseWorld implements PgRecord
{
    public static PgRecordPauseWorld of()
    {
        return new PgRecordPauseWorld();
    }

    @Override
    public UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmSenderSnapshot thread)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (thread == null) {
            throw new ArgumentNullException("thread");
        }
        action.fetchThread = UpcsmSenderSnapshotCreateSlot.of(thread);
        return null;
    }

    private PgRecordPauseWorld()
    {
    }
}
