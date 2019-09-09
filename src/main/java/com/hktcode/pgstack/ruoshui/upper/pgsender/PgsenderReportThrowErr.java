/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.snapshot.SnapshotReport;

public class PgsenderReportThrowErr
{
    public static PgsenderReportThrowErr of(long actionStart, Throwable throwsError)
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new PgsenderReportThrowErr(actionStart, throwsError);
    }

    public final long actionStart;

    public final Throwable throwsError;

    private PgsenderReportThrowErr(long actionStart, Throwable throwsError)
    {
        this.actionStart = actionStart;
        this.throwsError = throwsError;
    }
}
