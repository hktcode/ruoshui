/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.method.SimpleMethodDelResult;
import com.hktcode.bgsimple.method.SimpleMethodGetResult;
import com.hktcode.bgsimple.method.SimpleMethodPstResult;
import com.hktcode.bgsimple.method.SimpleMethodPutResult;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInner;
import com.hktcode.pgstack.ruoshui.pgsql.snapshot.PgsqlSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class MainlineActionNormal
    /* */< W extends MainlineActionNormal<W, C, M>
    /* */, C extends MainlineConfigNormal
    /* */, M extends MainlineMetricNormal
    /* */> //
    implements MainlineAction<W>
{
    private static final Logger logger = LoggerFactory.getLogger(MainlineActionNormal.class);

    public final C config;

    public final M metric;

    public final AtomicReference<SimpleStatus> status;

    public final TransferQueue<MainlineRecord> tqueue;

    protected MainlineActionNormal //
        /* */( C config //
        /* */, M metric //
        /* */, AtomicReference<SimpleStatus> status //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */) //
    {
        this.config = config;
        this.metric = metric;
        this.status = status;
        this.tqueue = tqueue;
    }


    @Override
    public SimpleMethodPstResult<W> pst()
    {
        return null;
    }

    @Override
    public SimpleMethodPutResult<W> put()
    {
        return null;
    }

    @Override
    public SimpleMethodGetResult<W> get()
    {
        return null;
    }

    @Override
    public SimpleMethodDelResult<W> del()
    {
        return null;
    }

    @Override
    public SimpleStatusInner newStatus(W action) throws InterruptedException
    {
        return null;
    }

    public <T> T pollFromFuture(Future<T> future)
        throws SQLException, InterruptedException
    {
        long waitTimeout = this.config.waitTimeout;
        long logDuration = this.config.logDuration;
        long starts = System.currentTimeMillis();
        T result = PgsqlSnapshot.pollFromFuture(future, waitTimeout);
        long finish = System.currentTimeMillis();
        ++metric.fetchCounts;
        metric.fetchMillis += (finish - starts);
        if (result != null) {
            ++metric.recordCount;
        }
        else if (finish - metric.logDatetime >= logDuration) {
            logger.info("get future timeout: waitTimeout={}, logDuration={}" //
                , waitTimeout, logDuration);
            metric.logDatetime = finish;
        }
        return result;
    }

    public MainlineRecordNormal send(MainlineRecordNormal record)
        throws InterruptedException
    {
        long waitTimeout = config.waitTimeout;
        long startsMillis = System.currentTimeMillis();
        metric.statusInfor = "offer record wait";
        boolean success = this.tqueue.tryTransfer(record, waitTimeout, TimeUnit.MILLISECONDS);
        metric.statusInfor = "offer record end";
        long finishMillis = System.currentTimeMillis();
        metric.offerMillis += (finishMillis - startsMillis);
        ++metric.offerCounts;
        if (success) {
            ++metric.recordCount;
            return null;
        }
        else {
            long logDuration = config.logDuration;
            long currMillis = System.currentTimeMillis();
            if (currMillis - metric.logDatetime >= logDuration) {
                logger.info("tryTransfer record to tqueue: waitTimeout={}, logDuration={}", waitTimeout, logDuration);
                metric.logDatetime = currMillis;
            }
            return record;
        }
    }
}
