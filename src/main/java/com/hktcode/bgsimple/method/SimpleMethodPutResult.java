/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgmethod.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public interface SimpleMethodPutResult<W extends BgWorker<W, M>, M> //
    extends SimpleMethodPut<W, M>
{
    @Override
    default SimpleMethodPutResult<W, M> run(W worker, M metric)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        return this;
    }
}
