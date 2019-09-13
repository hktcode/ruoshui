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

public abstract class PgAction //
    extends TqueueAction<PgAction, PgConfig, PgRecord> //
    implements BgWorker<PgAction> //
{
    protected PgAction //
        /* */( PgConfig config //
        /* */, TransferQueue<PgRecord> tqueue //
        /* */, AtomicReference<SimpleStatus> status //
        /* */)
    {
        super(config, tqueue, status);
    }

    @Override
    public PgResult pst()
    {
        return this.get();
    }

    @Override
    public PgResult put()
    {
        return this.get();
    }

    @Override
    public abstract PgResult get();

    @Override
    public abstract PgResultEnd del();

    public PgResult pst(LogSequenceNumber lsn)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        return this.get();
    }

    public PgResult pst(PgConfigSnapshot config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return this.get();
    }

    public PgActionThrowsErrors next(Throwable throwsError)
        throws InterruptedException
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return PgActionThrowsErrors.of(this, throwsError);
    }

    public PgActionTerminateEnd next() throws InterruptedException
    {
        return PgActionTerminateEnd.of(this);
    }

    public abstract PgMetric toMetrics();
}
