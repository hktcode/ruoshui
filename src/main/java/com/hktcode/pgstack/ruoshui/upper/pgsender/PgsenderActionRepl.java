/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.tqueue.TqueueAction;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.jdbc.PgConnection;

import java.sql.SQLException;

abstract class PgsenderActionRepl //
    extends TqueueAction<PgsenderAction<PgRecord, MainlineConfig>, MainlineConfig, PgRecord> //
    implements PgsenderAction<PgRecord, MainlineConfig> //
{
    public final long actionStart;

    public long fetchCounts = 0;

    public long fetchMillis = 0;

    protected PgsenderActionRepl(PgsenderActionData<PgRecord, MainlineConfig> action, long actionStart)
    {
        super(action.config, action.tqueue, action.status);
        this.actionStart = actionStart;
        this.logDatetime = action.logDatetime;
    }

    abstract PgsenderAction<PgRecord, MainlineConfig> next(PgConnection pgrepl) //
        throws SQLException, InterruptedException;

    public abstract PgsenderMetricRun toRunMetrics();

    @Override
    public PgsenderResultRun<PgRecord, MainlineConfig> get()
    {
        MainlineConfig config = this.config;
        PgsenderMetricRun metric = this.toRunMetrics();
        return PgsenderResultRun.of(config, metric);
    }

    @Override
    public PgsenderResultEnd<PgRecord, MainlineConfig, PgsenderMetricEnd> del()
    {
        MainlineConfig config = this.config;
        PgsenderMetricEnd metric = this.toEndMetrics();
        return PgsenderResultEnd.of(config, metric);
    }

    @Override
    public PgsenderActionThrowsErrors<PgRecord, MainlineConfig>
    next(Throwable throwsError)
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return PgsenderActionThrowsErrors.of //
            /* */( this.config //
            /* */, this.tqueue //
            /* */, this.status //
            /* */, this.toEndMetrics().toErrMetrics(throwsError) //
            /* */);
    }
}
