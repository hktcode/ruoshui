/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import org.postgresql.jdbc.PgConnection;

import java.sql.SQLException;

abstract class PgsenderActionRepl extends PgsenderAction
{
    public final long actionStart;

    public long fetchCounts = 0;

    public long fetchMillis = 0;

    protected PgsenderActionRepl(PgsenderActionData action, long actionStart)
    {
        super(action.config, action.tqueue, action.status);
        this.actionStart = actionStart;
        this.logDatetime = action.logDatetime;
    }

    abstract PgsenderAction next(PgConnection pgrepl) //
        throws SQLException, InterruptedException;

    public abstract PgsenderMetricRun toRunMetrics();

    @Override
    public PgsenderResultRun get()
    {
        PgsenderMetricRun metric = this.toRunMetrics();
        return PgsenderResultRun.of(this.config, metric);
    }

    @Override
    public PgsenderResultEnd<PgsenderMetricEnd> del()
    {
        PgsenderMetricEnd metric = this.toEndMetrics();
        return PgsenderResultEnd.of(this.config, metric);
    }
}
