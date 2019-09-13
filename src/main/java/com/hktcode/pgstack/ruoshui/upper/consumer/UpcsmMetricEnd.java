/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.hktcode.lang.exception.ArgumentNullException;

public class UpcsmMetricEnd
{
    public static UpcsmMetricEnd of(UpcsmMetricRun basicMetric)
    {
        if (basicMetric == null) {
            throw new ArgumentNullException("basicMetric");
        }
        return new UpcsmMetricEnd(basicMetric);
    }

    @JsonUnwrapped
    public final UpcsmMetricRun basicMetric;

    public final long totalMillis;

    private UpcsmMetricEnd(UpcsmMetricRun basicMetric)
    {
        long finish = System.currentTimeMillis();
        this.basicMetric = basicMetric;
        this.totalMillis = finish - basicMetric.actionStart;
    }
}
