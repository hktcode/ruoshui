/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgmethod.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public interface SimpleMethodPstResult<W extends BgWorker<W, M>, M> //
    extends SimpleMethodPst<W, M>
{
    @Override
    default SimpleMethodPstResult<W, M> run(W worker, M metric)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        return this;
    }
}
