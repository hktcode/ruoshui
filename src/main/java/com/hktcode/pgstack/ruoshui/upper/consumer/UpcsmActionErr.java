/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public class UpcsmActionErr //
    extends SimpleWorker<UpcsmAction> implements UpcsmAction
{
    public static UpcsmActionErr of //
        (UpcsmActionRun action, UpcsmReportSender fetchThread, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (fetchThread == null) {
            throw new ArgumentNullException("fetchThread");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new UpcsmActionErr(action, fetchThread, throwsError);
    }

    public static UpcsmActionErr of //
        (UpcsmActionEnd action, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new UpcsmActionErr(action, throwsError);
    }

    public final UpcsmMetricErr metric;

    private UpcsmActionErr //
        (UpcsmActionRun action, UpcsmReportSender fetchThread, Throwable throwsError)
    {
        super(action.status, 0);
        this.metric = UpcsmMetricErr.of(action, fetchThread, throwsError);
    }

    private UpcsmActionErr //
        (UpcsmActionEnd action, Throwable throwsError)
    {
        super(action.status, 0);
        this.metric = UpcsmMetricErr.of(action.metric, throwsError);
    }

    @Override
    public UpcsmActionErr next(Throwable throwsError) //
        throws InterruptedException
    {
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return this;
    }

    @Override
    public UpcsmResultErr get()
    {
        return UpcsmResultErr.of(metric);
    }
}
