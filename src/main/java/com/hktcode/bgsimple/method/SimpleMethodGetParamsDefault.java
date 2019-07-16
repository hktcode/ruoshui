/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleMethodGetParamsDefault<W extends BgWorker<W>> //
    implements SimpleMethodGetParams<W>
{
    public static <W extends BgWorker<W>> SimpleMethodGetParamsDefault<W> of()
    {
        return new SimpleMethodGetParamsDefault<>();
    }

    @Override
    public SimpleMethodGetResult<W> run(W wkstep)
    {
        if (wkstep == null) {
            throw new ArgumentNullException("wkstep");
        }
        return wkstep.get();
    }
}
