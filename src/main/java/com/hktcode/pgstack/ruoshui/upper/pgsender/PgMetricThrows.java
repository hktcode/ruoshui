/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.hktcode.lang.exception.ArgumentNullException;

import java.io.IOException;

public class PgMetricThrows implements PgMetricErr
{
    public static PgMetricThrows of(PgMetricRun runinfor, Throwable throwsError)
    {
        if (runinfor == null) {
            throw new ArgumentNullException("runinfor");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        long finish = System.currentTimeMillis();
        PgReportThrowErr throwerr = PgReportThrowErr.of(finish, throwsError);
        return new PgMetricThrows(runinfor, throwerr);
    }

    @JsonUnwrapped
    public final PgMetricRun runinfor;

    public final PgReportThrowErr throwerr;

    private PgMetricThrows(PgMetricRun runinfor, PgReportThrowErr throwerr)
    {
        this.runinfor = runinfor;
        this.throwerr = throwerr;
    }
}
