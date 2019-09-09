/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.tqueue.TqueueAction;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.lang.exception.NeverHappenAssertionError;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.sql.SQLException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public abstract class PgsenderActionData<R, C extends PgsenderConfig<R, C>> //
    extends TqueueAction<PgsenderAction<R, C>, C, R> //
    implements PgsenderAction<R, C> //
{
    private static final Logger logger = LoggerFactory.getLogger(PgsenderActionData.class);

    public final long actionStart;

    public long rsgetCounts = 0;

    public long rsgetMillis = 0;

    public long rsnextCount = 0;

    protected PgsenderActionData(PgsenderActionData<R, C> action, long actionStart)
    {
        super(action.config, action.tqueue, action.status);
        this.actionStart = actionStart;
        this.statusInfor = action.statusInfor;
        this.logDatetime = action.logDatetime;
    }

    protected PgsenderActionData //
        /* */( C config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<R> tqueue //
        /* */, long actionStart //
        /* */) //
    {
        super(config, tqueue, status);
        this.actionStart = actionStart;
    }

    public abstract PgsenderAction<R, C> //
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

    @Override
    public PgsenderActionThrowsErrors<R, C> next(Throwable throwsError)
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return PgsenderActionThrowsErrors.<R, C>of(this, throwsError);
    }

    public abstract PgsenderMetricRun toRunMetrics();

    @Override
    public PgsenderResult<R, C> get()
    {
        return PgsenderResultRun.of(this.config, this.toRunMetrics());
    }

    @Override
    public PgsenderResultEnd<R, C, PgsenderMetricEnd> del()
    {
        return PgsenderResultEnd.of(this.config, this.toEndMetrics());
    }
}
