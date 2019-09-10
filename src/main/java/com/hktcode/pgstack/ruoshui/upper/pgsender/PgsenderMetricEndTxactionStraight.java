/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricEndTxactionStraight extends PgsenderMetricEndTxaction
{
    static PgsenderMetricEndTxactionStraight of(PgActionReplTxactionStraight action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricEndTxactionStraight(action);
    }

    private PgsenderMetricEndTxactionStraight(PgActionReplTxactionStraight action)
    {
        super(action);
    }

    @Override
    public PgsenderMetricErrTxactionStraight toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return PgsenderMetricErrTxactionStraight.of(this, throwerr);
    }
}
