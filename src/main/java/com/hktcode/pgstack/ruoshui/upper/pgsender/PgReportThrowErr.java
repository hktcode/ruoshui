/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;

public class PgReportThrowErr
{
    public static PgReportThrowErr of(long actionStart, Throwable throwsError)
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new PgReportThrowErr(actionStart, throwsError);
    }

    public final long actionStart;

    public final Throwable throwsError;

    private PgReportThrowErr(long actionStart, Throwable throwsError)
    {
        this.actionStart = actionStart;
        this.throwsError = throwsError;
    }
}
