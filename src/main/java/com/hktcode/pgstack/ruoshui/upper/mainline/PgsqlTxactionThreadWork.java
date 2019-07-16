/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public class PgsqlTxactionThreadWork extends PgsqlTxactionThread
{
    static PgsqlTxactionThreadWork of
        /* */( Thread thread //
        /* */, PgsqlTxactionMetricNormalTxaction metric //
        /* */, TransferQueue<PgsqlTxactionRecord> tqueue //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        if (thread == null) {
            throw new ArgumentNullException("thread");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (tqueue == null) {
            throw new ArgumentNullException("tqueue");
        }
        return new PgsqlTxactionThreadWork(thread, metric, tqueue, status);
    }

    private final PgsqlTxactionMetricNormalTxaction metric;

    @Override
    public void setTxactionLsn(LogSequenceNumber lsn)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        // metric.txactionLsn = lsn;
    }

    private PgsqlTxactionThreadWork
        /* */( Thread thread //
        /* */, PgsqlTxactionMetricNormalTxaction metric //
        /* */, TransferQueue<PgsqlTxactionRecord> tqueue //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        super(thread, tqueue, status);
        this.metric = metric;
    }
}
