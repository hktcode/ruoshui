/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;

public class UpcsmFetchRecordSnapshotPauseWorld implements UpcsmFetchRecordSnapshot
{
    public static UpcsmFetchRecordSnapshotPauseWorld of()
    {
        return new UpcsmFetchRecordSnapshotPauseWorld();
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

    private UpcsmFetchRecordSnapshotPauseWorld()
    {
    }
}
