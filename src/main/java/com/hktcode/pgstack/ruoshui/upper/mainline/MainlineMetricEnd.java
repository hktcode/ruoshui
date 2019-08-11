/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

public interface MainlineMetricEnd extends MainlineMetric
{
    MainlineMetricErr toErrMetrics(Throwable throwerr);
}
