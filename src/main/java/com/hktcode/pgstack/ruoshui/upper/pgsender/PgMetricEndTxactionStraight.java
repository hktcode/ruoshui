/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricEndTxactionStraight extends PgMetricEndTxaction
{
    static PgMetricEndTxactionStraight of(PgActionReplTxactionStraight action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgMetricEndTxactionStraight(action);
    }

    private PgMetricEndTxactionStraight(PgActionReplTxactionStraight action)
    {
        super(action);
    }

    @Override
    public PgMetricErrTxactionStraight toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return PgMetricErrTxactionStraight.of(this, throwerr);
    }
}
