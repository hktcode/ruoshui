/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInner;
import com.hktcode.bgsimple.tqueue.TqueueAction;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.replication.LogSequenceNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.sql.SQLException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public abstract class MainlineActionData
    /* */< C extends MainlineConfig
    /* */> //
    extends TqueueAction<C, MainlineRecord> implements MainlineAction //
{
    private static final Logger logger = LoggerFactory.getLogger(MainlineActionData.class);

    public long rsgetCounts = 0;

    public long rsgetMillis = 0;

    public long rsnextCount = 0;

    protected <T extends MainlineActionData<F>, F extends C> //
    MainlineActionData(T action, long actionStart)
    {
        super(action.config, action.tqueue, action.status, actionStart);
        this.statusInfor = action.statusInfor;
        this.logDatetime = action.logDatetime;
    }

    protected MainlineActionData //
        /* */( C config //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */, long actionStart //
        /* */) //
    {
        super(config, tqueue, status, actionStart);
    }

    abstract MainlineAction next(ExecutorService exesvc, PgConnection pgdata, PgConnection pgrepl) //
        throws SQLException, ScriptException, InterruptedException;

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
                // TODO:
                throw new RuntimeException(cause);
            }
        }
    }

    public abstract MainlineMetricRun toRunMetrics();

    @SuppressWarnings("unchecked")
    @Override
    public MainlineActionThrowsErrors nextThrowErr(Throwable throwsError)
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return MainlineActionThrowsErrors.of(this, throwsError);
    }

    @Override
    public MainlineResultRun pst()
    {
        MainlineConfig config = this.config;
        MainlineMetric metric = this.toRunMetrics();
        return MainlineResultRun.of(config, metric);
    }

    @Override
    public MainlineResultRun pst(LogSequenceNumber lsn)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        return this.pst();
    }

    @Override
    public MainlineResultRun put()
    {
        MainlineConfig config = this.config;
        MainlineMetric metric = this.toRunMetrics();
        return MainlineResultRun.of(config, metric);
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
    public SimpleStatusInner newStatus(MainlineAction wkstep) throws InterruptedException
    {
        return null;
    }

    // MainlineActionTerminateEnd nextComplete();
}
