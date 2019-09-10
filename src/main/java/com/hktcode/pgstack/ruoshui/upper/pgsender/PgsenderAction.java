/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;
import org.postgresql.replication.LogSequenceNumber;

public interface PgsenderAction extends BgWorker<PgsenderAction>
{
    @Override
    default PgsenderResult pst()
    {
        return this.get();
    }

    @Override
    default PgsenderResult put()
    {
        return this.get();
    }

    @Override
    PgsenderResult get();

    @Override
    PgsenderResultEnd del();

    default PgsenderResult pst(LogSequenceNumber lsn)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        return this.get();
    }

    default PgsenderResult pst(SnapshotConfig config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return this.get();
    }

    PgsenderActionThrowsErrors next(Throwable throwsError);

    PgRecord send(PgRecord record) throws InterruptedException;

    PgsenderMetricEnd toEndMetrics();
}
