/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricEndTypelistStraight extends MainlineMetricEndTypelist
{
    static MainlineMetricEndTypelistStraight of(MainlineActionDataTypelistStraight action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineMetricEndTypelistStraight(action);
    }

    private MainlineMetricEndTypelistStraight(MainlineActionDataTypelistStraight action)
    {
        super(action);
    }

    @Override
    public MainlineMetricErrTypelistStraight toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return MainlineMetricErrTypelistStraight.of(this, throwerr);
    }
}
