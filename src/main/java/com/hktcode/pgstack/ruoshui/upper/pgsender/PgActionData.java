/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.lang.exception.NeverHappenAssertionError;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public abstract class PgActionData extends PgAction
{
    private static final Logger logger = LoggerFactory.getLogger(PgActionData.class);

    public final long actionStart;

    public long rsgetCounts = 0;

    public long rsgetMillis = 0;

    public long rsnextCount = 0;

    protected PgActionData(PgActionData action, long actionStart)
    {
        super(action.config, action.tqueue, action.status);
        this.actionStart = actionStart;
        this.statusInfor = action.statusInfor;
        this.logDatetime = action.logDatetime;
    }

    protected PgActionData //
        /* */(PgConfig config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<PgRecord> tqueue //
        /* */, long actionStart //
        /* */) //
    {
        super(config, tqueue, status);
        this.actionStart = actionStart;
    }

    public abstract PgAction //
    next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
        throws SQLException, InterruptedException, ScriptException;

    public <T> T pollFromFuture(Future<T> future) //
        throws SQLException, InterruptedException
    {
        long waitTimeout = this.config.waitTimeout;
        long logDuration = this.config.logDuration;
        long starts = System.currentTimeMillis();
        T result = pollFromFuture(future, waitTimeout);
        long finish = System.currentTimeMillis();
        ++this.rsgetCounts;
        this.rsgetMillis += (finish - starts);
        if (result != null) {
            ++this.rsnextCount;
        }
        else if (finish - this.logDatetime >= logDuration) {
            logger.info("future.get timeout: waitTimeout={}, logDuration={}" //
                , waitTimeout, logDuration);
            this.logDatetime = finish;
        }
        return result;
    }

    private static <T> T pollFromFuture(Future<T> future, long waitTimeout) //
        throws SQLException, InterruptedException
    {
        try {
            return future.get(waitTimeout, TimeUnit.MILLISECONDS);
        }
        catch (TimeoutException ex) {
            return null;
        }
        catch (ExecutionException ex) {
            logger.error("pollFromFuture throws ExcecutionException", ex);
            Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            else if (cause instanceof SQLException) {
                throw (SQLException)cause;
            }
            else if (cause instanceof Error) {
                throw (Error)cause;
            }
            else {
                throw new NeverHappenAssertionError(cause);
            }
        }
    }

    PreparedStatement preparedStatement(PgConnection pg, String sql) //
        throws SQLException
    {
        if (pg == null) {
            throw new ArgumentNullException("pg");
        }
        if (sql == null) {
            throw new ArgumentNullException("sql");
        }
        PreparedStatement ps = pg.prepareStatement //
            /* */( sql //
                /* */, ResultSet.TYPE_FORWARD_ONLY //
                /* */, ResultSet.CONCUR_READ_ONLY //
                /* */, ResultSet.CLOSE_CURSORS_AT_COMMIT //
                /* */);
        try {
            ps.setFetchDirection(ResultSet.FETCH_FORWARD);
            ps.setFetchSize(this.config.rsFetchsize);
            return ps;
        }
        catch (Exception ex) {
            ps.close();
            throw ex;
        }
    }

    public abstract PgMetricRun toRunMetrics();

    public PgMetric toMetrics()
    {
        return this.toRunMetrics();
    }

    @Override
    public PgResult get()
    {
        return PgResultNormal.of(this.config, this.toRunMetrics());
    }

    @Override
    public PgResultFinish del()
    {
        return PgResultFinish.of(this.config, this.toRunMetrics().toEndMetrics());
    }
}
