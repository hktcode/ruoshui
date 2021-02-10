package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public interface SimpleMethodResult<W extends BgWorker<W>> extends SimpleMethod<W>
{
    default SimpleMethodResult<W> run(W wkstep)
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        // TODO: logger this should not be happened
        return this;
    }
}
