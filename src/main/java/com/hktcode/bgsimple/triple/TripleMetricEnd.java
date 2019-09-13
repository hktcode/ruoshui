/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.triple;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.hktcode.lang.exception.ArgumentNullException;

public class TripleMetricEnd<R extends TripleMetricRun>
{
    public static <R extends TripleMetricRun> //
    TripleMetricEnd<R> of(R basicMetric)
    {
        if (basicMetric == null) {
            throw new ArgumentNullException("basicMetric");
        }
        return new TripleMetricEnd<>(basicMetric);
    }

    @JsonUnwrapped
    public final R basicMetric;

    protected TripleMetricEnd(R basicMetric)
    {
        this.basicMetric = basicMetric;
    }
}
