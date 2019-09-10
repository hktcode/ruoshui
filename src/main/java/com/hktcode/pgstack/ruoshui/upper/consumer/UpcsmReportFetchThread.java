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
        /* */(PgsenderResult mainline //
        /* */, ImmutableList<PgsenderResult> snapshot //
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

    public final PgsenderResult mainline;

    public final ImmutableList<PgsenderResult> snapshot;

    protected UpcsmReportFetchThread //
        /* */( PgsenderResult mainline
        /* */, ImmutableList<PgsenderResult> snapshot
        /* */)
    {
        this.mainline = mainline;
        this.snapshot = snapshot;
    }
}
