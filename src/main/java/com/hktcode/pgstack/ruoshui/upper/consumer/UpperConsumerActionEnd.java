/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;

import java.util.concurrent.BlockingQueue;

public class UpperConsumerActionEnd //
    extends SimpleWorker<UpperConsumerAction> implements UpperConsumerAction
{
    public static UpperConsumerActionEnd of(UpperConsumerActionRun action)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        return new UpperConsumerActionEnd(action);
    }

    public final UpperConsumerMetricEnd metric;

    public final BlockingQueue<UpperConsumerRecord> comein;

    private UpperConsumerActionEnd(UpperConsumerActionRun action)
    {
        super(action.status, 0);
        this.metric = UpperConsumerMetricEnd.of(action);
        this.comein = action.comein;
    }

    @Override
    public UpperConsumerActionErr next(Throwable throwable) //
        throws InterruptedException
    {
        if (throwable == null) {
            throw new ArgumentNullException("throwable");
        }
        return UpperConsumerActionErr.of(this, throwable);
    }

    @Override
    public UpperConsumerResultEnd pst()
    {
        return UpperConsumerResultEnd.of(metric);
    }

    @Override
    public UpperConsumerResultEnd put()
    {
        return UpperConsumerResultEnd.of(metric);
    }

    @Override
    public UpperConsumerResultEnd get()
    {
        return UpperConsumerResultEnd.of(metric);
    }

    @Override
    public UpperConsumerResultEnd del()
    {
        return UpperConsumerResultEnd.of(metric);
    }
}
