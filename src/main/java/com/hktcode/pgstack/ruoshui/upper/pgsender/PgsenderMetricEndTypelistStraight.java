/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgsenderMetricEndTypelistStraight extends PgsenderMetricEndTypelist
{
    static PgsenderMetricEndTypelistStraight of(PgActionDataTypelistStraight action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgsenderMetricEndTypelistStraight(action);
    }

    private PgsenderMetricEndTypelistStraight(PgActionDataTypelistStraight action)
    {
        super(action);
    }

    @Override
    public PgsenderMetricErrTypelistStraight toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return PgsenderMetricErrTypelistStraight.of(this, throwerr);
    }
}
