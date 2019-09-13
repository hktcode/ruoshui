/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.triple.TripleMetricRun;
import com.hktcode.lang.exception.ArgumentNullException;

public class UpcsmMetricRun extends TripleMetricRun
{
    public static UpcsmMetricRun of(UpcsmActionRun action, UpcsmReportSender fetchThread)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (fetchThread == null) {
            throw new ArgumentNullException("fetchThread");
        }
        return new UpcsmMetricRun(action, fetchThread);
    }

    public final UpcsmReportSender fetchThread;

    private UpcsmMetricRun(UpcsmActionRun action, UpcsmReportSender fetchThread)
    {
        super(action);
        this.fetchThread = fetchThread;
    }
}
