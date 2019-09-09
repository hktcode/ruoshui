/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineMetricEndTxactionStraight extends MainlineMetricEndTxaction
{
    static MainlineMetricEndTxactionStraight of(MainlineActionReplTxactionStraight action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineMetricEndTxactionStraight(action);
    }

    private MainlineMetricEndTxactionStraight(MainlineActionReplTxactionStraight action)
    {
        super(action);
    }

    @Override
    public MainlineMetricErrTxactionStraight toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return MainlineMetricErrTxactionStraight.of(this, throwerr);
    }
}
