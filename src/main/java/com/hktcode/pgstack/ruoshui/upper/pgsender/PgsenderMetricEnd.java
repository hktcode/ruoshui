/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

public interface PgsenderMetricEnd extends PgsenderMetric
{
    PgsenderMetricErr toErrMetrics(Throwable throwerr);
}
