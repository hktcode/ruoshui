package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

import java.util.concurrent.TransferQueue;

class MainlineThreadWork extends MainlineThread
{
    static MainlineThreadWork of
        /* */( Thread thread //
        /* */, MainlineMetricTxaction metric //
        /* */, TransferQueue<MainlineRecord> tqueue //
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
        return new MainlineThreadWork(thread, metric, tqueue);
    }

    private final MainlineMetricTxaction metric;

    @Override
    public void setTxactionLsn(LogSequenceNumber lsn)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        metric.txactionLsn = lsn;
    }

    private MainlineThreadWork
        /* */( Thread thread //
        /* */, MainlineMetricTxaction metric //
        /* */, TransferQueue<MainlineRecord> tqueue //
        /* */)
    {
        super(thread, tqueue);
        this.metric = metric;
    }
}
