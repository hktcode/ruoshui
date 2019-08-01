/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

class MainlineReportRelaLock
{
    static MainlineReportRelaLock of(long totalPeriod)
    {
        return new MainlineReportRelaLock(totalPeriod);
    }

    public final long totalPeriod;

    private MainlineReportRelaLock(long totalPeriod)
    {
        this.totalPeriod = totalPeriod;
    }
}
