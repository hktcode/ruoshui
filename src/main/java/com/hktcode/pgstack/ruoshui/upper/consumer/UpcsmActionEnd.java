/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public class UpcsmActionEnd //
    extends SimpleWorker<UpcsmAction> implements UpcsmAction
{
    public static UpcsmActionEnd of(UpcsmActionRun action, UpcsmReportSender fetchThread)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (fetchThread == null) {
            throw new ArgumentNullException("fetchThread");
        }
        return new UpcsmActionEnd(action, fetchThread);
    }

    public final UpcsmMetricEnd metric;

    private UpcsmActionEnd(UpcsmActionRun action, UpcsmReportSender fetchThread)
    {
        super(action.status, 0);
        this.metric = UpcsmMetricEnd.of(action, fetchThread);
    }

    @Override
    public UpcsmActionErr next(Throwable throwsError) //
        throws InterruptedException
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return UpcsmActionErr.of(this, throwsError);
    }

    @Override
    public UpcsmResultEnd get()
    {
        return UpcsmResultEnd.of(metric);
    }
}
