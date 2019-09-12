/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.pgsender;

import com.google.common.collect.ImmutableList;
import com.hktcode.bgsimple.method.SimpleMethodAllResultEnd;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInner;
import com.hktcode.bgsimple.status.SimpleStatusInnerEnd;
import com.hktcode.bgsimple.status.SimpleStatusInnerRun;
import com.hktcode.lang.exception.ArgumentNullException;

public class PgActionThrowsErrors extends PgAction
{
    public static PgActionThrowsErrors of(PgAction action, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new PgActionThrowsErrors(action, throwsError);
    }

    private PgActionThrowsErrors(PgAction action, Throwable throwsError)
    {
        super(action.config, action.tqueue, action.status);
        this.metric = action.toMetrics().toErrMetrics(throwsError);
    }

    public final PgMetricErr metric;

    @Override
    public PgActionTerminateEnd next() throws InterruptedException
    {
        SimpleStatusInner origin;
        PgResultThrows result = this.del();
        ImmutableList<SimpleMethodAllResultEnd> list = ImmutableList.of(result);
        SimpleStatusInnerEnd future = SimpleStatusInnerEnd.of(list);
        do {
            origin = this.newStatus(this);
        } while (    origin instanceof SimpleStatusInnerRun
                  && !this.status.compareAndSet(origin, future));
        return PgActionTerminateEnd.of(this);
    }

    @Override
    public PgMetricErr toMetrics()
    {
        return this.metric;
    }

    @Override
    public PgResultThrows get()
    {
        return PgResultThrows.of(config, metric);
    }

    @Override
    public PgResultThrows del()
    {
        return PgResultThrows.of(config, metric);
    }
}
