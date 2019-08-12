/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.SimpleWorker;
import com.hktcode.bgsimple.method.SimpleMethodDelResult;
import com.hktcode.bgsimple.method.SimpleMethodGetResult;
import com.hktcode.bgsimple.method.SimpleMethodPstResult;
import com.hktcode.bgsimple.method.SimpleMethodPutResult;
import com.hktcode.bgsimple.status.SimpleStatus;
import com.hktcode.bgsimple.status.SimpleStatusInner;
import com.hktcode.lang.exception.ArgumentNullException;
import com.hktcode.pgstack.ruoshui.upper.entity.UpperConsumerRecord;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineConfig;
import com.hktcode.pgstack.ruoshui.upper.mainline.MainlineMetricEnd;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

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
    public UpperConsumerAction next(Throwable throwable) throws InterruptedException
    {
        return null;
    }

    @Override
    public SimpleMethodPstResult<UpperConsumerActionEnd> pst()
    {
        return null;
    }

    @Override
    public SimpleMethodPutResult<UpperConsumerActionEnd> put()
    {
        return null;
    }

    @Override
    public SimpleMethodGetResult<UpperConsumerActionEnd> get()
    {
        return null;
    }

    @Override
    public SimpleMethodDelResult<UpperConsumerActionEnd> del()
    {
        return null;
    }
}
