/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

class PgActionReplTxactionStraight extends PgActionReplTxaction
{
    static PgActionReplTxactionStraight of(PgActionDataTypelistStraight action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgActionReplTxactionStraight(action);
    }

    private PgActionReplTxactionStraight(PgActionDataTypelistStraight action)
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
