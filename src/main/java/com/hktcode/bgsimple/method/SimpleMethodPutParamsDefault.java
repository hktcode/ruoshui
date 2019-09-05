/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleMethodPutParamsDefault<W extends BgWorker<W>> //
    implements SimpleMethodPutParams<W>
{
    public static <W extends BgWorker<W>> SimpleMethodPutParamsDefault<W> of()
    {
        return new SimpleMethodPutParamsDefault<>();
    }

    private SimpleMethodPutParamsDefault()
    {
    }

    @Override
    public SimpleMethodPutResult<W> run(W wkstep) throws InterruptedException
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        return wkstep.put();
    }
}
