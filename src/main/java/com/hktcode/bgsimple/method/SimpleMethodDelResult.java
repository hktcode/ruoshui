/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgmethod.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public interface SimpleMethodDelResult<T extends BgWorker<T, M>, M> //
    extends SimpleMethodDel<T, M>
{
    @Override
    default SimpleMethodDelResult<T, M> run(T worker, M metric)
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
