/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.pgsender.MainlineConfig;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgRecord;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgsenderResult;
import com.hktcode.pgstack.ruoshui.upper.pgsender.SnapshotConfig;

public class UpcsmReportFetchThread
{
    public static UpcsmReportFetchThread of //
        /* */(PgsenderResult<PgRecord, MainlineConfig> mainline //
        /* */, ImmutableList<PgsenderResult<com.hktcode.pgstack.ruoshui.upper.pgsender.PgRecord, SnapshotConfig>> snapshot //
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

    public final PgsenderResult<PgRecord, MainlineConfig> mainline;

    public final ImmutableList<PgsenderResult<com.hktcode.pgstack.ruoshui.upper.pgsender.PgRecord, SnapshotConfig>> snapshot;

    protected UpcsmReportFetchThread //
        /* */( PgsenderResult<PgRecord, MainlineConfig> mainline
        /* */, ImmutableList<PgsenderResult<com.hktcode.pgstack.ruoshui.upper.pgsender.PgRecord, SnapshotConfig>> snapshot
        /* */)
    {
        this.mainline = mainline;
        this.snapshot = snapshot;
    }
}
