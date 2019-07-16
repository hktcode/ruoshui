/*
 * Copyright (c) 2019, Huang Ketian.
 */
package com.hktcode.bgsimple.method;

import com.hktcode.bgsimple.BgWorker;
import com.hktcode.lang.exception.ArgumentNullException;

public class SimpleMethodPstParamsDefault<W extends BgWorker<W>> //
    implements SimpleMethodPstParams<W>
{
    public static <W extends BgWorker<W>> SimpleMethodPstParamsDefault<W> of()
    {
        return new SimpleMethodPstParamsDefault<>();
    }

    private SimpleMethodPstParamsDefault()
    {
    }

    @Override
    public SimpleMethodPstResult<W> run(W wkstep)
    {
        if (wkstep == null) {
            throw new ArgumentNullException("worker");
        }
        return wkstep.pst();
    }
}
