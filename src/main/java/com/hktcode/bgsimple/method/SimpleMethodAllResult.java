/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgmethod.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public interface SimpleMethodAllResult<T extends BgWorker<T, M>, M>
    extends SimpleMethodGetResult<T, M> //
    /*  */, SimpleMethodDelResult<T, M> //
    /*  */, SimpleMethodPstResult<T, M> //
    /*  */, SimpleMethodPutResult<T, M>
{
    @Override
    default SimpleMethodAllResult<T, M> run(T worker, M metric)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        // TODO: logger this should not be happened
        return this;
    }
}
