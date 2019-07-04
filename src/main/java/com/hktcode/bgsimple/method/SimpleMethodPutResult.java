/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgmethod.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

import java.time.ZonedDateTime;

public interface SimpleMethodPutResult<T extends BgWorker<T, M>, M> //
    extends SimpleMethodPut<T, M>
{
    @Override
    default SimpleMethodPutResult<T, M> run(T worker, M metric)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return this;
    }
}
