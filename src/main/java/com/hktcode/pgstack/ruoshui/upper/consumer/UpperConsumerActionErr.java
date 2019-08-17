/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineConfig;

import java.util.concurrent.BlockingQueue;

public class UpperConsumerActionErr //
    extends SimpleWorker<UpperConsumerActionErr> //
    implements UpperConsumerAction<UpperConsumerActionErr>
{
    public static UpperConsumerActionErr of //
        (UpperConsumerActionRun action, String statusInfor, Throwable throwsError)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (statusInfor == null) {
            throw new ArgumentNullException("statusInfor");
        }
        if (throwsError == null) {
            throw new ArgumentNullException("throwsError");
        }
        return new UpperConsumerActionErr(action, statusInfor, throwsError);
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

    public final MainlineConfig config;

    public final UpperConsumerMetricErr metric;

    public final BlockingQueue<UpperConsumerRecord> comein;

    private UpperConsumerActionErr //
        (UpperConsumerActionRun action, String statusInfor, Throwable throwsError)
    {
        super(action.status, 3);
        this.config = action.config;
        this.metric = UpperConsumerMetricErr.of(action, statusInfor, throwsError);
        this.comein = action.comein;
    }

    private UpperConsumerActionErr //
        (UpperConsumerActionEnd action, Throwable throwsError)
    {
        super(action.status, 3);
        this.config = action.config;
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
        return UpperConsumerResultErr.of(config, metric);
    }

    @Override
    public UpperConsumerResultErr put()
    {
        return UpperConsumerResultErr.of(config, metric);
    }

    @Override
    public UpperConsumerResultErr get()
    {
        return UpperConsumerResultErr.of(config, metric);
    }

    @Override
    public UpperConsumerResultErr del()
    {
        return UpperConsumerResultErr.of(config, metric);
    }
}
