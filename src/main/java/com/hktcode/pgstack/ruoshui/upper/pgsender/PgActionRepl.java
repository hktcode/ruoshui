/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import org.postgresql.jdbc.PgConnection;

import java.sql.SQLException;

abstract class PgActionRepl extends PgAction
{
    public final long actionStart;

    long fetchCounts = 0;

    long fetchMillis = 0;

    PgActionRepl(PgActionData action, long actionStart)
    {
        super(action.config, action.tqueue, action.status);
        this.actionStart = actionStart;
        this.logDatetime = action.logDatetime;
    }

    abstract PgAction next(PgConnection pgrepl) //
        throws SQLException, InterruptedException;

    public abstract PgMetricRun toRunMetrics();

    public PgMetric toMetrics()
    {
        return this.toRunMetrics();
    }

    @Override
    public PgResultNormal get()
    {
        PgMetricRun metric = this.toRunMetrics();
        return PgResultNormal.of(this.config, metric);
    }

    @Override
    public PgResultFinish del()
    {
        PgMetricEnd metric = this.toMetrics().toEndMetrics();
        return PgResultFinish.of(this.config, metric);
    }
}
