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

class MainlineActionThrowsErrors implements MainlineAction
{
    public static <T extends MainlineActionData<C>, C extends MainlineConfig>
    MainlineActionThrowsErrors of(T action, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new MainlineActionThrowsErrors(action, throwsError);
    }

    public static <T extends MainlineActionRepl<C>, C extends MainlineConfig>
    MainlineActionThrowsErrors of(T action, Throwable throwsError)
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

    public final MainlineConfig config;

    public final MainlineMetricErr metric;

    public final AtomicReference<SimpleStatus> status;

    public final TransferQueue<MainlineRecord> tqueue;

    private MainlineActionThrowsErrors(MainlineActionTerminateEnd action, Throwable throwsError)
    {
        this.config = action.config;
        this.metric = action.metric.toErrMetrics(throwsError);
        this.status = action.status;
        this.tqueue = action.tqueue;
    }

    private <T extends MainlineActionData<C>, C extends MainlineConfig> //
    MainlineActionThrowsErrors(T action, Throwable throwsError)
    {
        this.config = action.config;
        this.metric = action.toEndMetrics().toErrMetrics(throwsError);
        this.status = action.status;
        this.tqueue = action.tqueue;
    }

    private <T extends MainlineActionRepl<C>, C extends MainlineConfig> //
    MainlineActionThrowsErrors(T action, Throwable throwsError)
    {
        this.config = action.config;
        this.metric = action.toEndMetrics().toErrMetrics(throwsError);
        this.status = action.status;
        this.tqueue = action.tqueue;
    }

    @Override
    public MainlineResultEnd pst()
    {
        return MainlineResultEnd.of(this.config, this.metric);
    }

    @Override
    public MainlineResultEnd pst(LogSequenceNumber lsn)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        return MainlineResultEnd.of(this.config, this.metric);
    }

    @Override
    public MainlineResultEnd put()
    {
        return MainlineResultEnd.of(this.config, this.metric);
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
    public SimpleStatusInner newStatus(MainlineAction wkstep) //
        throws InterruptedException
    {
        return null; // TODO
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
