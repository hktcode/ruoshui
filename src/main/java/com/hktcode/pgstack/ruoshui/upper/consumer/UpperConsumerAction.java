/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.pgstack.ruoshui.upper.consumer;

import com.hktcode.bgsimple.BgWorker;

public interface UpperConsumerAction<A extends UpperConsumerAction<A>> //
    extends BgWorker<A>
{
    UpperConsumerActionErr next(Throwable throwable) throws InterruptedException;
}
