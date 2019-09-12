/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

public interface PgMetric
{
    PgMetricErr toErrMetrics(Throwable throwerr);

    PgMetricEnd toEndMetrics();
}
