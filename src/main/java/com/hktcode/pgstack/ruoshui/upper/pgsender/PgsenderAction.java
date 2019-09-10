/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.tqueue.TqueueAction;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class PgsenderAction //
    extends TqueueAction<PgsenderAction, PgsenderConfig, PgRecord> //
    implements BgWorker<PgsenderAction> //
{
    protected PgsenderAction //
        /* */( PgsenderConfig config //
        /* */, TransferQueue<PgRecord> tqueue //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        super(config, tqueue, status);
    }

    @Override
    public PgsenderResult pst()
    {
        return this.get();
    }

    @Override
    public PgsenderResult put()
    {
        return this.get();
    }

    @Override
    public abstract PgsenderResult get();

    @Override
    public abstract PgsenderResultEnd del();

    public PgsenderResult pst(LogSequenceNumber lsn)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        return this.get();
    }

    public PgsenderResult pst(SnapshotConfig config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return this.get();
    }

    public PgsenderActionThrowsErrors next(Throwable throwsError)
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return PgsenderActionThrowsErrors.of(this, throwsError);
    }

    public abstract PgsenderMetricEnd toEndMetrics();
}
