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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

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

    @Override
    public UpperConsumerAction next(Throwable throwable) throws InterruptedException
    {
        return null;
    }

    @Override
    public SimpleMethodPstResult<UpperConsumerActionErr> pst()
    {
        return null;
    }

    @Override
    public SimpleMethodPutResult<UpperConsumerActionErr> put()
    {
        return null;
    }

    @Override
    public SimpleMethodGetResult<UpperConsumerActionErr> get()
    {
        return null;
    }

    @Override
    public SimpleMethodDelResult<UpperConsumerActionErr> del()
    {
        return null;
    }
}
