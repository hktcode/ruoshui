/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpcsmFetchRecordSnapshotExecThrows implements UpcsmFetchRecordSnapshot
{
    public static UpcsmFetchRecordSnapshotExecThrows of(Throwable throwable)
    {
        if (throwable == null) {
            throw new ArgumentNullException("throwable");
        }
        return new UpcsmFetchRecordSnapshotExecThrows(throwable);
    }

    public final Throwable throwable;

    private static final Logger logger = LoggerFactory.getLogger(UpcsmFetchRecordSnapshotExecThrows.class);

    @Override
    public UpperRecordConsumer toRecord(UpcsmActionRun action, UpcsmThreadSnapshot thread)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (thread == null) {
            throw new ArgumentNullException("fetch");
        }
        logger.error("throws exception: ", throwable);
        action.fetchThread = thread.mlxact;
        return null;
    }

    private UpcsmFetchRecordSnapshotExecThrows(Throwable throwable)
    {
        this.throwable = throwable;
    }
}
