/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperRecordConsumer;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgRecordPauseWorld;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgRecordExecThrows;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgRecordExecFinish;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class UpcsmThreadSnapshotLockingRel extends UpcsmThreadSnapshot
{
    public static UpcsmThreadSnapshotLockingRel of //
        /* */(UpcsmThreadMainline mlxact //
        /* */, Thread thread //
        /* */, TransferQueue<PgRecord> tqueue //
        /* */, AtomicReference<SimpleStatus> status //
        /* */) //
    {
        if (mlxact == null) {
            throw new ArgumentNullException("mlxact");
        }
        if (thread == null) {
            throw new ArgumentNullException("thread");
        }
        if (tqueue == null) {
            throw new ArgumentNullException("tqueue");
        }
        if (status == null) {
            throw new ArgumentNullException("status");
        }
        return new UpcsmThreadSnapshotLockingRel(mlxact, thread, tqueue, status);
    }

    private UpcsmThreadSnapshotLockingRel
        /* */(UpcsmThreadMainline mlxact //
        /* */, Thread thread //
        /* */, TransferQueue<PgRecord> tqueue //
        /* */, AtomicReference<SimpleStatus> status //
        /* */) //
    {
        super(mlxact, thread, tqueue, status);
    }

    private static final Logger logger = LoggerFactory.getLogger(UpcsmThreadSnapshotLockingRel.class);

    @Override
    public UpperRecordConsumer poll(long timeout, UpcsmActionRun action) //
        throws InterruptedException
    {
        PgRecord record = this.tqueue.poll(timeout, TimeUnit.MILLISECONDS);
        if (   (record instanceof PgRecordPauseWorld)
            || (record instanceof PgRecordExecThrows)
            || (record instanceof PgRecordExecFinish)) {
            return record.toRecord(action, this);
        }
        else if (this.thread.isAlive()) {
            return mlxact.poll(timeout, action);
        }
        else {
            logger.error("snapshot post is not alive."); // TODO:
            // TODO: mlxact.sslist.add();
            action.fetchThread = mlxact;
            return null;
        }
    }
}
