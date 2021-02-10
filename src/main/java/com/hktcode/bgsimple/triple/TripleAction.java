/*
 * Copyright (c) 2019, Huang Ketian.
 */

package com.hktcode.bgsimple.triple;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.bgsimple.status.SimpleStatusInner;
import com.hktcode.bgsimple.tqueue.TqueueConfig;

public interface TripleAction
    /* */< A extends TripleAction<A, C, M>
    /* */, C extends TqueueConfig
    /* */, M extends TripleMetricRun
    /* */>
    extends BgWorker<A>
{
    TripleActionErr<A, C, M> next(Throwable throwsError) throws InterruptedException;

    SimpleStatusInner newStatus(A action) throws InterruptedException;

    default TripleResult<A> get() throws InterruptedException
    {
        return this.del();
    }

    default TripleResult<A> pst() throws InterruptedException
    {
        return this.get();
    }

    default TripleResult<A> put() throws InterruptedException
    {
        return this.get();
    }

    @Override
    TripleResultEnd<A, C, M> del() throws InterruptedException;
}
