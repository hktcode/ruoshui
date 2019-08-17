/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineConfig;

import java.util.concurrent.BlockingQueue;

public class UpperConsumerActionEnd //
    extends SimpleWorker<UpperConsumerActionEnd> //
    implements UpperConsumerAction<UpperConsumerActionEnd>
{
    public static UpperConsumerActionEnd of //
        (UpperConsumerActionRun action, String statusInfor)
    {
        if (action == null) {
            throw new ArgumentNullException("action");
        }
        if (statusInfor == null) {
            throw new ArgumentNullException("statusInfor");
        }
        return new UpperConsumerActionEnd(action, statusInfor);
    }

    public final MainlineConfig config;

    public final UpperConsumerMetricEnd metric;

    public final BlockingQueue<UpperConsumerRecord> comein;

    private UpperConsumerActionEnd(UpperConsumerActionRun action, String statusInfor)
    {
        super(action.status, 3);
        this.config = action.config;
        this.metric = UpperConsumerMetricEnd.of(action, statusInfor);
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
        return UpperConsumerResultEnd.of(config, metric);
    }

    @Override
    public UpperConsumerResultEnd put()
    {
        return UpperConsumerResultEnd.of(config, metric);
    }

    @Override
    public UpperConsumerResultEnd get()
    {
        return UpperConsumerResultEnd.of(config, metric);
    }

    @Override
    public UpperConsumerResultEnd del()
    {
        return UpperConsumerResultEnd.of(config, metric);
    }
}
