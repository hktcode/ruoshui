/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;

class MainlineActionDataTypelistStraight //
    extends MainlineActionDataTypelist
{
    static MainlineActionDataTypelistStraight of(MainlineActionDataBegin1stStraight action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineActionDataTypelistStraight(action);
    }

    private MainlineActionDataTypelistStraight(MainlineActionDataBegin1stStraight action)
    {
        super(action);
    }

    public MainlineActionReplTxactionStraight txaction()
    {
        return MainlineActionReplTxactionStraight.of(this);
    }

    @Override
    public MainlineMetricRunTypelistStraight toRunMetrics()
    {
        return MainlineMetricRunTypelistStraight.of(this);
    }

    @Override
    public MainlineMetricEndTypelistStraight toEndMetrics()
    {
        return MainlineMetricEndTypelistStraight.of(this);
    }
}

