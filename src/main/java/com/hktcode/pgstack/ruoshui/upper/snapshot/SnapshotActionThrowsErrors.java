/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.bgsimple.tqueue.TqueueAction;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmFetchRecordSnapshot;

class SnapshotActionThrowsErrors
    extends TqueueAction<SnapshotAction, SnapshotConfig, UpcsmFetchRecordSnapshot>
    implements SnapshotAction
{
    public static SnapshotActionThrowsErrors of //
        /* */( SnapshotActionData action //
        /* */, Throwable throwsError //
        /* */)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new SnapshotActionThrowsErrors(action, throwsError);
    }

    public static SnapshotActionThrowsErrors of //
        /* */( SnapshotActionTerminateEnd action //
        /* */, Throwable throwsError //
        /* */)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new SnapshotActionThrowsErrors(action, throwsError);
    }

    private SnapshotActionThrowsErrors //
        /* */( SnapshotActionData action //
        /* */, Throwable throwsError //
        /* */)
    {
        super(action.config, action.tqueue, action.status);
        this.metric = action.toEndMetrics().toErrMetrics(throwsError);
    }

    private SnapshotActionThrowsErrors //
        /* */( SnapshotActionTerminateEnd action //
        /* */, Throwable throwsError //
        /* */)
    {
        super(action.config, action.tqueue, action.status);
        this.metric = action.toEndMetrics().toErrMetrics(throwsError);
    }

    public final SnapshotMetricErr metric;

    @Override
    public SnapshotActionThrowsErrors next(Throwable throwsError)
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return this;
    }

    @Override
    public SnapshotMetricEnd toEndMetrics()
    {
        return this.metric;
    }

    @Override
    public SnapshotResultErr get()
    {
        return SnapshotResultErr.of(config, metric);
    }

    @Override
    public SnapshotResultErr del()
    {
        return SnapshotResultErr.of(config, metric);
    }
}
