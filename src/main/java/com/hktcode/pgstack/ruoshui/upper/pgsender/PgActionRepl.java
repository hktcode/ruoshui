/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import org.postgresql.jdbc.PgConnection;

import java.sql.SQLException;

abstract class PgActionRepl extends PgAction
{
    public final long actionStart;

    public long fetchCounts = 0;

    public long fetchMillis = 0;

    protected PgActionRepl(PgActionData action, long actionStart)
    {
        super(action.config, action.tqueue, action.status);
        this.actionStart = actionStart;
        this.logDatetime = action.logDatetime;
    }

    abstract PgAction next(PgConnection pgrepl) //
        throws SQLException, InterruptedException;

    public abstract PgMetricRun toRunMetrics();

    @Override
    public PgsenderResultRun get()
    {
        PgMetricRun metric = this.toRunMetrics();
        return PgsenderResultRun.of(this.config, metric);
    }

    @Override
    public PgsenderResultEnd<PgMetricEnd> del()
    {
        PgMetricEnd metric = this.toEndMetrics();
        return PgsenderResultEnd.of(this.config, metric);
    }
}
