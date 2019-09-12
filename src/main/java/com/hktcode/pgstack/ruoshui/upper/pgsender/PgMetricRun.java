/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public interface PgMetricRun extends PgMetric
{
    @Override
    default PgMetricErr toErrMetrics(Throwable throwerr)
    {
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return PgMetricThrows.of(this, throwerr);
    }

    @Override
    default PgMetricEnd toEndMetrics()
    {
        return PgMetricFinish.of(this);
    }
}
