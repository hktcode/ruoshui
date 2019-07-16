/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public interface SimpleMethodAllResult<W extends BgWorker<W>>
    extends SimpleMethodGetResult<W> //
    /*  */, SimpleMethodDelResult<W> //
    /*  */, SimpleMethodPstResult<W> //
    /*  */, SimpleMethodPutResult<W>
{
    @Override
    default SimpleMethodAllResult<W> run(W wkstep)
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        // TODO: logger this should not be happened
        return this;
    }
}
