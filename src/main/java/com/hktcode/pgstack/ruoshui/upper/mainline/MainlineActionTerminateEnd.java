/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInner;
import com.hktcode.lang.exception.ArgumentNullException;

import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class MainlineActionTerminateEnd implements MainlineAction<MainlineActionTerminateEnd>
{
    public static <W extends MainlineActionData<W, C>, C extends MainlineConfig>
    MainlineActionTerminateEnd of(MainlineActionData<W, C> action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineActionTerminateEnd(action);
    }

    public static <W extends MainlineActionRepl<W, C>, C extends MainlineConfig>
    MainlineActionTerminateEnd of(MainlineActionRepl<W, C> action)
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

    private <W extends MainlineActionData<W, C>, C extends MainlineConfig> //
    MainlineActionTerminateEnd(MainlineActionData<W, C> action)
    {
        this.config = action.config;
        this.tqueue = action.tqueue;
        this.status = action.status;
        this.metric = action.toEndMetrics();
    }

    private <W extends MainlineActionRepl<W, C>, C extends MainlineConfig> //
    MainlineActionTerminateEnd(MainlineActionRepl<W, C> action)
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
    public MainlineResultRun<MainlineActionTerminateEnd> pst()
    {
        return MainlineResultRun.of(this.config, this.metric);
    }

    @Override
    public MainlineResultRun<MainlineActionTerminateEnd> put()
    {
        return MainlineResultRun.of(this.config, this.metric);
    }

    @Override
    public MainlineResultRun<MainlineActionTerminateEnd> get()
    {
        return MainlineResultRun.of(this.config, this.metric);
    }

    @Override
    public MainlineResultEnd<MainlineActionTerminateEnd> del()
    {
        return MainlineResultEnd.of(this.config, this.metric);
    }

    @Override
    public SimpleStatusInner newStatus(MainlineActionTerminateEnd wkstep) throws InterruptedException
    {
        return null;
    }
}
