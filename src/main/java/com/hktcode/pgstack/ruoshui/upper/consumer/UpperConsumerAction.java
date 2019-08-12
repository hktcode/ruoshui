/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.bgsimple.status.SimpleStatusInner;

public interface UpperConsumerAction<A extends UpperConsumerAction<A>> //
    extends BgWorker<A>
{
    UpperConsumerAction next(Throwable throwable) throws InterruptedException;
}
