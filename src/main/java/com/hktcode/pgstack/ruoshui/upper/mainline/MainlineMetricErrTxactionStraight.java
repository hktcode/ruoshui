/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricErrTxactionStraight extends MainlineMetricErrTxaction
{
    static MainlineMetricErrTxactionStraight of(MainlineMetricEndTxactionStraight metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return new MainlineMetricErrTxactionStraight(metric, throwerr);
    }

    private MainlineMetricErrTxactionStraight(MainlineMetricEndTxactionStraight metric, Throwable throwerr)
    {
        super(metric, throwerr);
    }
}
