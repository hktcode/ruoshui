/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public class UpcsmActionEnd //
    extends SimpleWorker<UpcsmAction> implements UpcsmAction
{
    public static UpcsmActionEnd of(UpcsmActionRun action) //
        throws InterruptedException
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new UpcsmActionEnd(action);
    }

    public final UpcsmMetricEnd metric;

    private UpcsmActionEnd(UpcsmActionRun action) //
        throws InterruptedException
    {
        super(action.status, 0);
        this.metric = UpcsmMetricEnd.of(action.get().metric);
    }

    @Override
    public UpcsmActionErr next(Throwable throwsError) //
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
