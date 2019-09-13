/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public class UpcsmActionErr //
    extends SimpleWorker<UpcsmAction> implements UpcsmAction
{
    public static UpcsmActionErr of(UpcsmActionRun action, Throwable throwsError) //
        throws InterruptedException
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new UpcsmActionErr(action, throwsError);
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

    private UpcsmActionErr(UpcsmActionRun action, Throwable throwsError) //
        throws InterruptedException
    {
        super(action.status, 0);
        UpcsmMetricRun basicMetric;
        if (throwsError instanceof FetchThreadThrowsErrorException) {
            UpcsmReportSender report = ((FetchThreadThrowsErrorException) throwsError).sender;
            basicMetric = UpcsmMetricRun.of(action, report);
        }
        else {
            basicMetric = action.get().metric;
        }
        this.metric = UpcsmMetricErr.of(basicMetric, throwsError);
    }

    private UpcsmActionErr(UpcsmActionEnd action, Throwable throwsError)
    {
        super(action.status, 0);
        this.metric = UpcsmMetricErr.of(action.metric.basicMetric, throwsError);
    }

    @Override
    public UpcsmActionErr next(Throwable throwsError) //
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
