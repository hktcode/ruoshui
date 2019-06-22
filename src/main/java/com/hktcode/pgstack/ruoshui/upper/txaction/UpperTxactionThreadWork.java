/*
 * Copyright (c) 2019, Huang Ketian
 */
package com.hktcode.pgstack.ruoshui.upper.txaction;

import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

import java.util.concurrent.TransferQueue;

public class UpperTxactionThreadWork extends UpperTxactionThread
{
    public static UpperTxactionThreadWork of
        /* */( Thread thread //
        /* */, UpperTxactionMetric metric //
        /* */, TransferQueue<UpperTxactionRecord> tqueue //
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
        return new UpperTxactionThreadWork(thread, metric, tqueue);
    }

    private final UpperTxactionMetric metric;

    @Override
    public void setTxactionLsn(LogSequenceNumber lsn)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        metric.txactionLsn = lsn;
    }

    private UpperTxactionThreadWork
        /* */( Thread thread //
        /* */, UpperTxactionMetric metric //
        /* */, TransferQueue<UpperTxactionRecord> tqueue //
        /* */)
    {
        super(thread, tqueue);
        this.metric = metric;
    }
}
