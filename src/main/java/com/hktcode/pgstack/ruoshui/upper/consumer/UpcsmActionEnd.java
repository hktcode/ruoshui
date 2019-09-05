/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public class UpcsmActionEnd //
    extends SimpleWorker<UpcsmAction> implements UpcsmAction
{
    public static UpcsmActionEnd of(UpcsmActionRun action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new UpcsmActionEnd(action);
    }

    public final UpcsmMetricEnd metric;

    private UpcsmActionEnd(UpcsmActionRun action)
    {
        super(action.status, 0);
        this.metric = UpcsmMetricEnd.of(action);
    }

    @Override
    public UpcsmActionErr next(Throwable throwable) //
        throws InterruptedException
    {
        if (throwable == null) {
            throw new ArgumentNullException("throwable");
        }
        return UpcsmActionErr.of(this, throwable);
    }

    @Override
    public UpcsmResultEnd pst()
    {
        return UpcsmResultEnd.of(metric);
    }

    @Override
    public UpcsmResultEnd put()
    {
        return UpcsmResultEnd.of(metric);
    }

    @Override
    public UpcsmResultEnd get()
    {
        return UpcsmResultEnd.of(metric);
    }

    @Override
    public UpcsmResultEnd del()
    {
        return UpcsmResultEnd.of(metric);
    }
}
