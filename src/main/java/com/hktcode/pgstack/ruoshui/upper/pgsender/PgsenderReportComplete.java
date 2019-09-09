/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

public class PgsenderReportComplete
{
    public final long actionStart;

    public static PgsenderReportComplete of(long actionStart)
    {
        return new PgsenderReportComplete(actionStart);
    }

    private PgsenderReportComplete(long actionStart)
    {
        this.actionStart = actionStart;
    }
}
