/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.snapshot;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.consumer.UpcsmFetchRecordSnapshot;
import org.postgresql.replication.LogSequenceNumber;

interface SnapshotAction extends BgWorker<SnapshotAction>
{
    @Override
    default SnapshotResult pst()
    {
        return this.get();
    }

    @Override
    default SnapshotResult put()
    {
        return this.get();
    }

    @Override
    SnapshotResult get();

    @Override
    SimpleMethodAllResultEnd<SnapshotAction> del();

    default SnapshotResult pst(LogSequenceNumber lsn)
    {
        if (lsn == null) {
            throw new ArgumentNullException("lsn");
        }
        return this.get();
    }

    SnapshotActionThrowsErrors next(Throwable throwsError);

    UpcsmFetchRecordSnapshot send(UpcsmFetchRecordSnapshot record) throws InterruptedException;

    SnapshotMetricEnd toEndMetrics();
}
