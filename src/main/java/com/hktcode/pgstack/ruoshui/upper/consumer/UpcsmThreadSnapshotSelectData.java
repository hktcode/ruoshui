/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class UpcsmThreadSnapshotSelectData extends UpcsmThreadSnapshot
{
    private static final Logger logger //
        = LoggerFactory.getLogger(UpcsmThreadSnapshotSelectData.class);

    public static UpcsmThreadSnapshotSelectData of
        /* */(UpcsmThreadSnapshot thread //
        /* */, UpperRecordConsumer record //
        /* */) //
    {
        if (thread == null) {
            throw new ArgumentNullException("thread");
        }
        if (record == null) {
            throw new ArgumentNullException("record");
        }
        return new UpcsmThreadSnapshotSelectData(thread, record);
    }

    private final UpperRecordConsumer record;

    private UpcsmThreadSnapshotSelectData
        /* */(UpcsmThreadSnapshot thread //
        /* */, UpperRecordConsumer record //
        /* */) //
    {
        super(thread);
        this.record = record;
    }

    @Override
    public UpperRecordConsumer poll(long timeout, UpcsmActionRun action)
        throws InterruptedException
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        UpcsmFetchRecordSnapshot r = this.tqueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (   (r instanceof UpcsmFetchRecordSnapshotLogicalMsg)
            || (r instanceof UpcsmFetchRecordSnapshotExecFinish)
            || (r instanceof UpcsmFetchRecordSnapshotExecThrows)) {
            UpperRecordConsumer result = r.toRecord(action, this);
            return action.fetchThread == mlxact ? this.record : result;
        }
        else if (thread.isAlive()) {
            return null;
        }
        else {
            logger.error("snapshot post is not alive."); // TODO:
            action.fetchThread = mlxact;
            return this.record;
        }
    }
}
