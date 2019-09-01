/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.bgsimple.status.SimpleStatusInner;

public interface UpperConsumerAction extends BgWorker<UpperConsumerAction>
{
    UpperConsumerActionErr next(Throwable throwable) throws InterruptedException;

    SimpleStatusInner newStatus(UpperConsumerAction wkstep) throws InterruptedException;
}
