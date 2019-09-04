/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.UpperConsumerRecord;

import java.util.concurrent.BlockingQueue;

public class UpperConsumerActionErr //
    extends SimpleWorker<UpperConsumerAction> implements UpperConsumerAction
{
    public static UpperConsumerActionErr of //
        (UpperConsumerActionRun action, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new UpperConsumerActionErr(action, throwsError);
    }

    public static UpperConsumerActionErr of //
        (UpperConsumerActionEnd action, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new UpperConsumerActionErr(action, throwsError);
    }

    public final UpperConsumerMetricErr metric;

    public final BlockingQueue<UpperConsumerRecord> comein;

    private UpperConsumerActionErr //
        (UpperConsumerActionRun action, Throwable throwsError)
    {
        super(action.status, 0);
        this.metric = UpperConsumerMetricErr.of(action, throwsError);
        this.comein = action.comein;
    }

    private UpperConsumerActionErr //
        (UpperConsumerActionEnd action, Throwable throwsError)
    {
        super(action.status, 0);
        this.metric = UpperConsumerMetricErr.of(action.metric, throwsError);
        this.comein = action.comein;
    }

    @Override
    public UpperConsumerActionErr next(Throwable throwable) //
        throws InterruptedException
    {
        return this;
    }

    @Override
    public UpperConsumerResultErr pst()
    {
        return UpperConsumerResultErr.of(metric);
    }

    @Override
    public UpperConsumerResultErr put()
    {
        return UpperConsumerResultErr.of(metric);
    }

    @Override
    public UpperConsumerResultErr get()
    {
        return UpperConsumerResultErr.of(metric);
    }

    @Override
    public UpperConsumerResultErr del()
    {
        return UpperConsumerResultErr.of(metric);
    }
}
