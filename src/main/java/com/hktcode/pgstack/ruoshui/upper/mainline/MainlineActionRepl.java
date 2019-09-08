/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.tqueue.TqueueAction;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.consumer.MainlineRecord;
import org.postgresql.jdbc.PgConnection;

import javax.script.ScriptException;
import java.sql.SQLException;

abstract class MainlineActionRepl //
    extends TqueueAction<MainlineAction, MainlineConfig, MainlineRecord> //
    implements MainlineAction //
{
    public final long actionStart;

    public long fetchCounts = 0;

    public long fetchMillis = 0;

    protected MainlineActionRepl(MainlineActionData action, long actionStart)
    {
        super(action.config, action.tqueue, action.status);
        this.actionStart = actionStart;
        this.logDatetime = action.logDatetime;
    }

    abstract MainlineAction next(PgConnection pgrepl) //
        throws SQLException, InterruptedException, ScriptException;

    public abstract MainlineMetricRun toRunMetrics();

    @Override
    public MainlineResultRun pst()
    {
        return this.get();
    }

    @Override
    public MainlineResultRun put()
    {
        return this.get();
    }

    @Override
    public MainlineResultRun get()
    {
        MainlineConfig config = this.config;
        MainlineMetric metric = this.toRunMetrics();
        return MainlineResultRun.of(config, metric);
    }

    @Override
    public MainlineResultEnd del()
    {
        MainlineConfig config = this.config;
        MainlineMetricEnd metric = this.toEndMetrics();
        return MainlineResultEnd.of(config, metric);
    }

    @Override
    public MainlineActionThrowsErrors nextThrowErr(Throwable throwsError)
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return MainlineActionThrowsErrors.of(this, throwsError);
    }
}
