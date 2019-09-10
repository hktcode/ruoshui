/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricErrTypelistStraight extends PgMetricErrTypelist
{
    static PgMetricErrTypelistStraight of(PgMetricEndTypelistStraight metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new PgMetricErrTypelistStraight(metric, throwerr);
    }

    private PgMetricErrTypelistStraight(PgMetricEndTypelistStraight metric, Throwable throwerr)
    {
        super(metric, throwerr);
    }
}
