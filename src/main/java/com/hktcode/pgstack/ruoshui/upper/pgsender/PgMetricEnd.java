/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

public interface PgMetricEnd extends PgMetric
{
    @Override
    default PgMetricEnd toEndMetrics()
    {
        return this;
    }
}
