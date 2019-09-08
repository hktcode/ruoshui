/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.tqueue.TqueueAction;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.consumer.MainlineRecord;
import org.postgresql.replication.LogSequenceNumber;

public class MainlineActionTerminateEnd
    extends TqueueAction<MainlineAction, MainlineConfig, MainlineRecord> //
    implements MainlineAction
{
    public static MainlineActionTerminateEnd of(MainlineActionData action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineActionTerminateEnd(action);
    }

    public static MainlineActionTerminateEnd of(MainlineActionRepl action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineActionTerminateEnd(action);
    }

    public final MainlineMetricEnd metric;

    private MainlineActionTerminateEnd(MainlineActionData action)
    {
        super(action.config, action.tqueue, action.status);
        this.metric = action.toEndMetrics();
    }

    private MainlineActionTerminateEnd(MainlineActionRepl action)
    {
        super(action.config, action.tqueue, action.status);
        this.metric = action.toEndMetrics();
    }

    @Override
    public MainlineActionThrowsErrors nextThrowErr(Throwable throwsError)
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return MainlineActionThrowsErrors.of(this, throwsError);
    }

    @Override
    public MainlineMetricEnd toEndMetrics()
    {
        return this.metric;
    }

    @Override
    public MainlineResultEnd pst()
    {
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
        return MainlineResultEnd.of(this.config, this.metric);
    }

    @Override
    public MainlineResultEnd pst(LogSequenceNumber lsn)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        return this.get();
    }
}
