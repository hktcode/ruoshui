/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgmethod.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public interface SimpleMethodPstResult<T extends BgWorker<T, M>, M> //
    extends SimpleMethodPst<T, M>
{
    @Override
    default SimpleMethodPstResult<T, M> run(T worker, M metric)
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
