/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

public class PgReportComplete
{
    public final long actionStart;

    public static PgReportComplete of(long actionStart)
    {
        return new PgReportComplete(actionStart);
    }

    private PgReportComplete(long actionStart)
    {
        this.actionStart = actionStart;
    }
}
