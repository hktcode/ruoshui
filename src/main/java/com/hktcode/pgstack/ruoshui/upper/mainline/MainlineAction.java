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

    @Override
    MainlineResultRun pst();

    @Override
    MainlineResultRun put();

    @Override
    MainlineResultRun get();

    @Override
    MainlineResultEnd del();

    MainlineResultRun pst(LogSequenceNumber lsn);
}
