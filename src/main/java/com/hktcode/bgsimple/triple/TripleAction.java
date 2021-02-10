/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.triple;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.bgsimple.tqueue.TqueueConfig;

public interface TripleAction<C extends TqueueConfig, M extends TripleMetricRun> extends BgWorker
{
    TripleActionErr<C, M> next(Throwable throwsError) throws InterruptedException;

    default TripleAction<C, M> next() throws Exception
    {
        return this;
    }

    default TripleResult get() throws InterruptedException
    {
        return this.del();
    }

    default TripleResult pst() throws InterruptedException
    {
        return this.get();
    }

    default TripleResult put() throws InterruptedException
    {
        return this.get();
    }

    @Override
    TripleResultEnd<C, M> del() throws InterruptedException;
}
