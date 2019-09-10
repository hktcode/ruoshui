/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgResult;

public class UpcsmReportSender
{
    public static UpcsmReportSender of //
        /* */(PgResult mainline //
        /* */, ImmutableList<PgResult> snapshot //
        /* */)
    {
        if (mainline == null) {
            throw new ArgumentNullException("mainline");
        }
        if (snapshot == null) {
            throw new ArgumentNullException("snapshot");
        }
        return new UpcsmReportSender(mainline, snapshot);
    }

    public final PgResult mainline;

    public final ImmutableList<PgResult> snapshot;

    protected UpcsmReportSender //
        /* */( PgResult mainline
        /* */, ImmutableList<PgResult> snapshot
        /* */)
    {
        this.mainline = mainline;
        this.snapshot = snapshot;
    }
}