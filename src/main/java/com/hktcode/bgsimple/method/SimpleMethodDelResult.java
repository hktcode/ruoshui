/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public interface SimpleMethodDelResult<W extends BgWorker<W, M>, M> //
    extends SimpleMethodDel<W, M>
{
    @Override
    default SimpleMethodDelResult<W, M> run(W worker, M metric)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        if (metric == null) {
            throw new ArgumentNullException("metric");
        }
        // TODO: logger this should not be happened
        return this;
    }
}
