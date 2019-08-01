/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

class MainlineReportSizeDiff
{
    static MainlineReportSizeDiff of(long totalPeriod)
    {
        return new MainlineReportSizeDiff(totalPeriod);
    }

    public final long totalPeriod;

    private MainlineReportSizeDiff(long totalPeriod)
    {
        this.totalPeriod = totalPeriod;
    }
}
