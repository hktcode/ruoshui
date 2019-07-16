/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleMethodDelParamsDefault<W extends BgWorker<W>> //
    implements SimpleMethodDelParams<W>
{
    public static <W extends BgWorker<W>> SimpleMethodDelParamsDefault<W> of()
    {
        return new SimpleMethodDelParamsDefault<>();
    }

    private SimpleMethodDelParamsDefault()
    {
    }

    @Override
    public SimpleMethodDelResult<W> run(W wkstep)
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        return wkstep.del();
    }
}
