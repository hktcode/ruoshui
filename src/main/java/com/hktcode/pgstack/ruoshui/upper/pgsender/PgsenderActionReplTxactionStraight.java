/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

class PgsenderActionReplTxactionStraight extends PgsenderActionReplTxaction
{
    static PgsenderActionReplTxactionStraight of(PgsenderActionDataTypelistStraight action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderActionReplTxactionStraight(action);
    }

    private PgsenderActionReplTxactionStraight(PgsenderActionDataTypelistStraight action)
    {
        super(action);
    }

    @Override
    public PgsenderMetricRunTxactionStraight toRunMetrics()
    {
        return PgsenderMetricRunTxactionStraight.of(this);
    }

    @Override
    public PgsenderMetricEndTxactionStraight toEndMetrics()
    {
        return PgsenderMetricEndTxactionStraight.of(this);
    }
}
