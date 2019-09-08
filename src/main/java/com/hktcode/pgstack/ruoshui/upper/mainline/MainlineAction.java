/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.consumer.MainlineRecord;
import com.hktcode.pgstack.ruoshui.upper.snapshot.SnapshotConfig;
import org.postgresql.replication.LogSequenceNumber;

interface MainlineAction extends BgWorker<MainlineAction>
{
    MainlineActionThrowsErrors nextThrowErr(Throwable throwsError);

    MainlineMetricEnd toEndMetrics();

    @SuppressWarnings("unchecked")
    @Override
    MainlineResult pst();

    @SuppressWarnings("unchecked")
    @Override
    MainlineResult put();

    @SuppressWarnings("unchecked")
    @Override
    MainlineResult get();

    @Override
    MainlineResultEnd del();

    MainlineResult pst(LogSequenceNumber lsn);

    default MainlineResult pst(SnapshotConfig config)
    {
        if (config == null) {
            throw new ArgumentNullException("config");
        }
        return this.get();
    }

    MainlineRecord send(MainlineRecord record) throws InterruptedException;
}
