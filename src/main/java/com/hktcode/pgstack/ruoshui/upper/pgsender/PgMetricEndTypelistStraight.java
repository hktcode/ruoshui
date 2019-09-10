/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgMetricEndTypelistStraight extends PgMetricEndTypelist
{
    static PgMetricEndTypelistStraight of(PgActionDataTypelistStraight action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new PgMetricEndTypelistStraight(action);
    }

    private PgMetricEndTypelistStraight(PgActionDataTypelistStraight action)
    {
        super(action);
    }

    @Override
    public PgMetricErrTypelistStraight toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return PgMetricErrTypelistStraight.of(this, throwerr);
    }
}
