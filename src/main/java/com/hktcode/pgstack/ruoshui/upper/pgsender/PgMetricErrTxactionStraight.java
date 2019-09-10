/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricErrTxactionStraight extends PgMetricErrTxaction
{
    static PgMetricErrTxactionStraight of(PgMetricEndTxactionStraight metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return new PgMetricErrTxactionStraight(metric, throwerr);
    }

    private PgMetricErrTxactionStraight(PgMetricEndTxactionStraight metric, Throwable throwerr)
    {
        super(metric, throwerr);
    }
}
