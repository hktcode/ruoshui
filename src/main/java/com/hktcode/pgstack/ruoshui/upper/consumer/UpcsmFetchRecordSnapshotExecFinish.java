/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;

public class UpcsmFetchRecordSnapshotExecFinish implements UpcsmFetchRecordSnapshot
{
    public static UpcsmFetchRecordSnapshotExecFinish of()
    {
        return new UpcsmFetchRecordSnapshotExecFinish();
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
        action.fetchThread = thread.mlxact;
        return null;
    }

    private UpcsmFetchRecordSnapshotExecFinish()
    {
    }
}
