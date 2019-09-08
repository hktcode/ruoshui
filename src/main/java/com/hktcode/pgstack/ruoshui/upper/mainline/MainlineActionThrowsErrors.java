/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.tqueue.TqueueAction;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.consumer.MainlineRecord;
import org.postgresql.replication.LogSequenceNumber;

class MainlineActionThrowsErrors
    extends TqueueAction<MainlineAction, MainlineConfig, MainlineRecord> //
    implements MainlineAction
{
    public static MainlineActionThrowsErrors of(MainlineActionData action, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new MainlineActionThrowsErrors(action, throwsError);
    }

    public static MainlineActionThrowsErrors of(MainlineActionRepl action, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new MainlineActionThrowsErrors(action, throwsError);
    }

    public static MainlineActionThrowsErrors //
    of(MainlineActionTerminateEnd action, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new MainlineActionThrowsErrors(action, throwsError);
    }

    public final MainlineMetricErr metric;

    private MainlineActionThrowsErrors(MainlineActionTerminateEnd action, Throwable throwsError)
    {
        super(action.config, action.tqueue, action.status);
        this.metric = action.metric.toErrMetrics(throwsError);
    }

    private MainlineActionThrowsErrors(MainlineActionData action, Throwable throwsError)
    {
        super(action.config, action.tqueue, action.status);
        this.metric = action.toEndMetrics().toErrMetrics(throwsError);
    }

    private MainlineActionThrowsErrors(MainlineActionRepl action, Throwable throwsError)
    {
        super(action.config, action.tqueue, action.status);
        this.metric = action.toEndMetrics().toErrMetrics(throwsError);
    }

    @Override
    public MainlineResultEnd pst()
    {
        return this.get();
    }

    @Override
    public MainlineResultEnd pst(LogSequenceNumber lsn)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        return this.get();
    }

    @Override
    public MainlineResultEnd put()
    {
        return this.get();
    }

    @Override
    public MainlineResultEnd get()
    {
        return MainlineResultEnd.of(this.config, this.metric);
    }

    @Override
    public MainlineResultEnd del()
    {
        return this.get();
    }

    @Override
    public MainlineActionThrowsErrors nextThrowErr(Throwable throwsError)
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return this;
    }

    @Override
    public MainlineMetricEnd toEndMetrics()
    {
        return this.metric;
    }
}
