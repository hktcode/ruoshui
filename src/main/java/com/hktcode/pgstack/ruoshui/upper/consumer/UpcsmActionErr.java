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
        (UpcsmActionRun action, Throwable throwsError)
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

    private UpcsmActionErr //
        (UpcsmActionRun action, Throwable throwsError)
    {
        super(action.status, 0);
        this.metric = UpcsmMetricErr.of(action, throwsError);
    }

    private UpcsmActionErr //
        (UpcsmActionEnd action, Throwable throwsError)
    {
        super(action.status, 0);
        this.metric = UpcsmMetricErr.of(action.metric, throwsError);
    }

    @Override
    public UpcsmActionErr next(Throwable throwable) //
        throws InterruptedException
    {
        return this;
    }

    @Override
    public UpcsmResultErr pst()
    {
        return UpcsmResultErr.of(metric);
    }

    @Override
    public UpcsmResultErr put()
    {
        return UpcsmResultErr.of(metric);
    }

    @Override
    public UpcsmResultErr get()
    {
        return UpcsmResultErr.of(metric);
    }

    @Override
    public UpcsmResultErr del()
    {
        return UpcsmResultErr.of(metric);
    }
}
