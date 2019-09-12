/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricFinish implements PgMetricEnd
{
    static PgMetricFinish of(PgMetricRun runinfor)
    {
        if (runinfor == null) {
            throw new ArgumentNullException("runinfor");
        }
        return new PgMetricFinish(runinfor);
    }

    private PgMetricFinish(PgMetricRun runinfor)
    {
        long finish = System.currentTimeMillis();
        this.runinfor = runinfor;
        this.complete = PgReportComplete.of(finish);
    }

    public final PgMetricRun runinfor;

    public final PgReportComplete complete;

    @Override
    public PgMetricErr toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return PgMetricThrows.of(this.runinfor, throwerr);
    }
}
