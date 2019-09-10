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
    public PgMetricRunTxactionStraight toRunMetrics()
    {
        return PgMetricRunTxactionStraight.of(this);
    }

    @Override
    public PgMetricEndTxactionStraight toEndMetrics()
    {
        return PgMetricEndTxactionStraight.of(this);
    }
}
