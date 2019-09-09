/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricErrTxactionStraight extends PgsenderMetricErrTxaction
{
    static PgsenderMetricErrTxactionStraight of(PgsenderMetricEndTxactionStraight metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return new PgsenderMetricErrTxactionStraight(metric, throwerr);
    }

    private PgsenderMetricErrTxactionStraight(PgsenderMetricEndTxactionStraight metric, Throwable throwerr)
    {
        super(metric, throwerr);
    }
}
