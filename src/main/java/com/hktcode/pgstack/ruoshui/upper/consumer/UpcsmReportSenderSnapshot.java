/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.google.common.collect.ImmutableList;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.pgsender.PgResult;

public class UpcsmReportSenderSnapshot extends UpcsmReportSender
{
    public static UpcsmReportSenderSnapshot of //
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
        return new UpcsmReportSenderSnapshot(mainline, snapshot);
    }

    private UpcsmReportSenderSnapshot //
        /* */(PgResult mainline //
        /* */, ImmutableList<PgResult> snapshot //
        /* */)
    {
        super(mainline, snapshot);
    }
}
