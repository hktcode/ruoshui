/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgmethod.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public interface SimpleMethodAllResult<W extends BgWorker<W, M>, M>
    extends SimpleMethodGetResult<W, M> //
    /*  */, SimpleMethodDelResult<W, M> //
    /*  */, SimpleMethodPstResult<W, M> //
    /*  */, SimpleMethodPutResult<W, M>
{
    @Override
    default SimpleMethodAllResult<W, M> run(W worker, M metric)
    {
        if (worker == null) {
            throw new ArgumentNullException("worker");
        }
        // TODO: logger this should not be happened
        return this;
    }
}
