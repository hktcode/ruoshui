/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.pgsender.MainlineConfig;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgsenderResult;
import com.hktcode.pgstack.ruoshui.upper.pgsender.SnapshotConfig;

public class UpcsmReportFetchThread
{
    public static UpcsmReportFetchThread of //
        /* */( PgsenderResult<MainlineRecord, MainlineConfig> mainline //
        /* */, ImmutableList<PgsenderResult<UpcsmFetchRecordSnapshot, SnapshotConfig>> snapshot //
        /* */)
    {
        if (mainline == null) {
            throw new ArgumentNullException("mainline");
        }
        if (snapshot == null) {
            throw new ArgumentNullException("snapshot");
        }
        return new UpcsmReportFetchThread(mainline, snapshot);
    }

    public final PgsenderResult<MainlineRecord, MainlineConfig> mainline;

    public final ImmutableList<PgsenderResult<UpcsmFetchRecordSnapshot, SnapshotConfig>> snapshot;

    protected UpcsmReportFetchThread //
        /* */( PgsenderResult<MainlineRecord, MainlineConfig> mainline
        /* */, ImmutableList<PgsenderResult<UpcsmFetchRecordSnapshot, SnapshotConfig>> snapshot
        /* */)
    {
        this.mainline = mainline;
        this.snapshot = snapshot;
    }
}
