/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInner;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class MainlineActionTerminateEnd implements MainlineAction
{
    public static <A extends MainlineActionData<C>, C extends MainlineConfig>
    MainlineActionTerminateEnd of(A action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineActionTerminateEnd(action);
    }

    public static <A extends MainlineActionRepl<C>, C extends MainlineConfig>
    MainlineActionTerminateEnd of(A action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineActionTerminateEnd(action);
    }

    public final MainlineConfig config;

    public final MainlineMetricEnd metric;

    public final TransferQueue<MainlineRecord> tqueue;

    public final AtomicReference<SimpleStatus> status;

    private <A extends MainlineActionData<C>, C extends MainlineConfig> //
    MainlineActionTerminateEnd(A action)
    {
        this.config = action.config;
        this.tqueue = action.tqueue;
        this.status = action.status;
        this.metric = action.toEndMetrics();
    }

    private <A extends MainlineActionRepl<C>, C extends MainlineConfig> //
    MainlineActionTerminateEnd(A action)
    {
        this.config = action.config;
        this.tqueue = action.tqueue;
        this.status = action.status;
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
    public MainlineResultRun pst()
    {
        return MainlineResultRun.of(this.config, this.metric);
    }

    @Override
    public MainlineResultRun put()
    {
        return MainlineResultRun.of(this.config, this.metric);
    }

    @Override
    public MainlineResultRun get()
    {
        return MainlineResultRun.of(this.config, this.metric);
    }

    @Override
    public MainlineResultEnd del()
    {
        return MainlineResultEnd.of(this.config, this.metric);
    }

    @Override
    public MainlineResultRun pst(LogSequenceNumber lsn)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        return MainlineResultRun.of(this.config, this.metric);
    }

    @Override
    public SimpleStatusInner newStatus(MainlineAction wkstep) //
        throws InterruptedException
    {
        return null;
    }
}
