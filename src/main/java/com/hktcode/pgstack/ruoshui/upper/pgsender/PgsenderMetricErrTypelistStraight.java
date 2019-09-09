/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricErrTypelistStraight extends PgsenderMetricErrTypelist
{
    static PgsenderMetricErrTypelistStraight of(PgsenderMetricEndTypelistStraight metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new PgsenderMetricErrTypelistStraight(metric, throwerr);
    }

    private PgsenderMetricErrTypelistStraight(PgsenderMetricEndTypelistStraight metric, Throwable throwerr)
    {
        super(metric, throwerr);
    }
}
