/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.hktcode.lang.exception.ArgumentNullException;

class MainlineActionReplTxactionStraight //
    extends MainlineActionReplTxaction<MainlineActionReplTxactionStraight>
{
    static MainlineActionReplTxactionStraight of(MainlineActionDataTypelistStraight action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new MainlineActionReplTxactionStraight(action);
    }

    private MainlineActionReplTxactionStraight(MainlineActionDataTypelistStraight action)
    {
        super(action);
    }

    public MainlineMetricRunTxactionStraight complete()
    {
        return MainlineMetricRunTxactionStraight.of(this);
    }

    @Override
    public MainlineMetricRunTxactionStraight toRunMetrics()
    {
        return MainlineMetricRunTxactionStraight.of(this);
    }

    @Override
    public MainlineMetricEndTxactionStraight toEndMetrics()
    {
        return MainlineMetricEndTxactionStraight.of(this);
    }
}

