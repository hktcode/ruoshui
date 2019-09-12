/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricThrows implements PgMetricErr
{
    public static PgMetricThrows of(PgMetricRun runinfor, Throwable throwsError)
    {
        if (runinfor == null) {
            throw new ArgumentNullException("runinfor");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        long finish = System.currentTimeMillis();
        PgReportThrowErr throwerr = PgReportThrowErr.of(finish, throwsError);
        return new PgMetricThrows(runinfor, throwerr);
    }

    public final PgMetricRun runinfor;

    public final PgReportThrowErr throwerr;

    private PgMetricThrows(PgMetricRun runinfor, PgReportThrowErr throwerr)
    {
        this.runinfor = runinfor;
        this.throwerr = throwerr;
    }
}
