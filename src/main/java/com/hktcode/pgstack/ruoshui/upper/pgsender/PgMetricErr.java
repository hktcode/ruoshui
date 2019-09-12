/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public interface PgMetricErr extends PgMetricEnd
{
    @Override
    default PgMetricErr toErrMetrics(Throwable throwerr)
    {
        // 为通用性着想，后续可能支持多次throwable的情况
        if (throwerr == null) {
            throw new ArgumentNullException("throwerr");
        }
        return this;
    }

    @Override
    default PgMetricEnd toEndMetrics()
    {
        return this;
    }
}
