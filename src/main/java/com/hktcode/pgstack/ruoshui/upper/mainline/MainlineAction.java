/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.bgsimple.BgWorker;
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

    MainlineRecord send(MainlineRecord record) throws InterruptedException;
}
