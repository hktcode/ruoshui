/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.hktcode.lang.exception.ArgumentNullException;

import java.io.IOException;

public class UpcsmMetricErr
{
    public static UpcsmMetricErr of(UpcsmMetricRun basicMetric, Throwable throwsError)
    {
        if (basicMetric == null) {
            throw new ArgumentNullException("basicMetric");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new UpcsmMetricErr(basicMetric, throwsError);
    }

    @JsonUnwrapped
    public final UpcsmMetricRun basicMetric;

    public final long totalMillis;

    public final UpcsmReportThrows throwsError;

    private UpcsmMetricErr(UpcsmMetricRun basicMetric, Throwable throwsError)
    {
        long finish = System.currentTimeMillis();
        this.basicMetric = basicMetric;
        this.totalMillis = finish - basicMetric.actionStart;
        this.throwsError = UpcsmReportThrows.of(finish, throwsError);
    }
}
