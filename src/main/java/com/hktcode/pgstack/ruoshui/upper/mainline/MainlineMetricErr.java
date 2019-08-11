/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

public interface MainlineMetricErr extends MainlineMetricEnd
{
    @Override
    default MainlineMetricErr toErrMetrics(Throwable throwerr)
    {
        // 为通用性着想，后续可能支持多次throwable的情况
        return this;
    }
}
