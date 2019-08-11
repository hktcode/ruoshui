/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricErrTypelistStraight extends MainlineMetricErrTypelist
{
    static MainlineMetricErrTypelistStraight of(MainlineMetricEndTypelistStraight metric, Throwable throwerr)
    {
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return new MainlineMetricErrTypelistStraight(metric, throwerr);
    }

    private MainlineMetricErrTypelistStraight(MainlineMetricEndTypelistStraight metric, Throwable throwerr)
    {
        super(metric, throwerr);
    }
}
