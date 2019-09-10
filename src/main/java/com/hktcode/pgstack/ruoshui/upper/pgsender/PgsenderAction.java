/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

public interface PgsenderAction<C extends PgsenderConfig> //
    extends BgWorker<PgsenderAction<C>>
{
    @Override
    default PgsenderResult<C> pst()
    {
        return this.get();
    }

    @Override
    default PgsenderResult<C> put()
    {
        return this.get();
    }

    @Override
    PgsenderResult<C> get();

    @Override
    @SuppressWarnings("unchecked")
    PgsenderResultEnd del();

    default PgsenderResult<C> pst(LogSequenceNumber lsn)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        return this.get();
    }

    default PgsenderResult<C> pst(SnapshotConfig config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return this.get();
    }

    PgsenderActionThrowsErrors<C> next(Throwable throwsError);

    PgRecord send(PgRecord record) throws InterruptedException;

    PgsenderMetricEnd toEndMetrics();
}
