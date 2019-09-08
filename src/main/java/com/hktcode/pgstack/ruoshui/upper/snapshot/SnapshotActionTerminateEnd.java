/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.bgsimple.tqueue.TqueueAction;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmFetchRecordSnapshot;
import org.postgresql.replication.LogSequenceNumber;

public class SnapshotActionTerminateEnd
    extends TqueueAction<SnapshotAction, SnapshotConfig, UpcsmFetchRecordSnapshot> //
    implements SnapshotAction
{
    public static <A extends SnapshotActionData>
    SnapshotActionTerminateEnd of(A action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new SnapshotActionTerminateEnd(action);
    }

    public final SnapshotMetricEnd metric;

    private <A extends SnapshotActionData>
    SnapshotActionTerminateEnd(A action)
    {
        super(action.config, action.tqueue, action.status);
        this.metric = action.toEndMetrics();
    }

    @Override
    public SnapshotActionThrowsErrors next(Throwable throwsError)
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return SnapshotActionThrowsErrors.of(this, throwsError);
    }

    @Override
    public SnapshotMetricEnd toEndMetrics()
    {
        return this.metric;
    }

    @Override
    public SnapshotResultEnd pst()
    {
        return this.get();
    }

    @Override
    public SnapshotResultEnd put()
    {
        return this.get();
    }

    @Override
    public SnapshotResultEnd get()
    {
        return SnapshotResultEnd.of(this.config, this.metric);
    }

    @Override
    public SnapshotResultEnd del()
    {
        return SnapshotResultEnd.of(this.config, this.metric);
    }

    @Override
    public SnapshotResultEnd pst(LogSequenceNumber lsn)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        return this.get();
    }
}
