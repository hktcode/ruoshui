/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.mainline;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hktcode.lang.exception.ArgumentNullException;

public class MainlineReportThrowErr implements MainlineReport
{
    public static MainlineReportThrowErr of(long actionStart, Throwable throwsError)
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new MainlineReportThrowErr(actionStart, throwsError);
    }

    public final long actionStart;

    public final Throwable throwsError;

    private MainlineReportThrowErr(long actionStart, Throwable throwsError)
    {
        this.actionStart = actionStart;
        this.throwsError = throwsError;
    }

    @Override
    public void toJsonObject(ObjectNode node)
    {
        node.put("action_start", actionStart);
        node.put("throws_err", throwsError.toString());
    }
}
