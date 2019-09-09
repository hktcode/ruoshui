/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

public interface PgsenderAction<R, C extends PgsenderConfig<R, C>> //
    extends BgWorker<PgsenderAction<R, C>>
{
    @Override
    default PgsenderResult<R, C> pst()
    {
        return this.get();
    }

    @Override
    default PgsenderResult<R, C> put()
    {
        return this.get();
    }

    @Override
    PgsenderResult<R, C> get();

    @Override
    @SuppressWarnings("unchecked")
    PgsenderResultEnd del();

    default PgsenderResult<R, C> pst(LogSequenceNumber lsn)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        return this.get();
    }

    default PgsenderResult<R, C> pst(SnapshotConfig config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return this.get();
    }

    PgsenderActionThrowsErrors<R, C> next(Throwable throwsError);

    R send(R record) throws InterruptedException;

    PgsenderMetricEnd toEndMetrics();
}
