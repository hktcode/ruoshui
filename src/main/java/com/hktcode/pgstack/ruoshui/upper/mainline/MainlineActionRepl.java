/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatusInner;
import com.hktcode.bgsimple.tqueue.TqueueAction;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.jdbc.PgConnection;

import java.sql.SQLException;

abstract class MainlineActionRepl
    /* */< W extends MainlineActionRepl<W, C>
    /* */, C extends MainlineConfig
    /* */> //
    extends TqueueAction<C, MainlineRecord> implements MainlineAction<W> //
{
    public long fetchCounts = 0;

    public long fetchMillis = 0;

    protected <T extends MainlineActionData<T, F>, F extends C>
    MainlineActionRepl(T action, long actionStart)
    {
        super(action.config, action.tqueue, action.status, actionStart);
        this.logDatetime = action.logDatetime;
    }

    abstract MainlineAction next(PgConnection pgrepl) //
        throws SQLException, InterruptedException;

    public abstract MainlineMetricRun toRunMetrics();

    @Override
    public MainlineResultRun<W> pst()
    {
        MainlineConfig config = this.config;
        MainlineMetric metric = this.toRunMetrics();
        return MainlineResultRun.of(config, metric);
    }

    @Override
    public MainlineResultRun<W> put()
    {
        MainlineConfig config = this.config;
        MainlineMetric metric = this.toRunMetrics();
        return MainlineResultRun.of(config, metric);
    }

    @Override
    public MainlineResultRun<W> get()
    {
        MainlineConfig config = this.config;
        MainlineMetric metric = this.toRunMetrics();
        return MainlineResultRun.of(config, metric);
    }

    @Override
    public MainlineResultEnd<W> del()
    {
        MainlineConfig config = this.config;
        MainlineMetricEnd metric = this.toEndMetrics();
        return MainlineResultEnd.of(config, metric);
    }

    @Override
    public SimpleStatusInner newStatus(W wkstep) throws InterruptedException
    {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MainlineActionThrowsErrors nextThrowErr(Throwable throwsError)
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return MainlineActionThrowsErrors.of((W)this, throwsError);
    }

    // MainlineActionTerminateEnd nextComplete();
}
