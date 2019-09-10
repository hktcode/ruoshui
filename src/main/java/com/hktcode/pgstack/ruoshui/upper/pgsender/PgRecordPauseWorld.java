/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmActionRun;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmFetchRecordSnapshot;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmThreadSnapshot;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmThreadSnapshotCreateSlot;

public class PgRecordPauseWorld implements UpcsmFetchRecordSnapshot
{
    public static PgRecordPauseWorld of()
    {
        return new PgRecordPauseWorld();
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
        action.fetchThread = UpcsmThreadSnapshotCreateSlot.of(thread);
        return null;
    }

    private PgRecordPauseWorld()
    {
    }
}
