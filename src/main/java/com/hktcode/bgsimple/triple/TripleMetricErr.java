/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.triple;

import com.hktcode.lang.exception.ArgumentNullException;

public class TripleMetricErr<R extends TripleMetricRun> extends TripleMetricEnd<R>
{
    public static <R extends TripleMetricRun> //
    TripleMetricErr<R> of(R basicMetric, TripleReportThrows throwsError)
    {
        if (basicMetric == null) {
            throw new ArgumentNullException("basicMetric");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new TripleMetricErr<>(basicMetric, throwsError);
    }

    public static <R extends TripleMetricRun> //
    TripleMetricErr<R> of(R basicMetric, Throwable throwsError)
    {
        if (basicMetric == null) {
            throw new ArgumentNullException("basicMetric");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new TripleMetricErr<>(basicMetric, throwsError);
    }

    public final TripleReportThrows throwsError;

    protected TripleMetricErr(R basicMetric, TripleReportThrows throwsError)
    {
        super(basicMetric);
        this.throwsError = throwsError;
    }

    protected TripleMetricErr(R basicMetric, Throwable throwsError)
    {
        super(basicMetric);
        long actionStart = basicMetric.actionStart + basicMetric.totalMillis;
        this.throwsError = TripleReportThrows.of(actionStart, throwsError);
    }
}
